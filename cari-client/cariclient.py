#!/usr/bin/env python3
import sys
import socket
import base64
import gzip
import json
import argparse
import subprocess

from argumentshandler import ArgumentsHandler
from preferencesresource import PreferencesResource
from sqliteresource import SqliteResource
from cmdprompt import CmdPrompt
from consolecolors import ConsoleColors
from cariexception import CARIException

'''
CARI (Console Android Resources Inspector)
Client used to communicate with Android device

How to pack to one file:
stickytape cariclient.py  --add-python-path . --output-file ./cari-client-standalone.py
'''
class CARIClient:

    # adb forward tcp:38300 tcp:38300
    # adb forward --remove-all
    # lsof -i :38300

    HOST = '127.0.0.1'
    PORT = 38300
    VERSION = '1.0.2'

    ENCODING = "UTF-8"
    LINE_ENDING = "\r\n"

    arguments_handler = ArgumentsHandler()
    preferences_resource = PreferencesResource()
    sqlite_resource = SqliteResource()
    port = None
    device = None
    initialized = False

    def __init__(self):
        print("CARI Console Android Resource Inspector, v{0}".format(self.VERSION))
        try:
            self.port, self.device = self.arguments_handler.process()
            self.check_status()
            self.initialized = True
        except CARIException as ce:
            self.print_colored(ce, ConsoleColors.WARNING)
        except:
            self.print_colored("Device with SDK is unavailable", ConsoleColors.FAIL)

    def check_status(self):
        version_request = "version"
        result = self.write_and_receive(version_request)
        response_map = json.loads(result)["response"]
        for key in response_map:
            self.print_colored("{0}: {1}".format(key, response_map[key]), ConsoleColors.OKGREEN, new_line='')
        print("\n")

    def execute(self):
        if self.initialized:
            try:
                cmd = CmdPrompt()
                cmd.request_callback = self.handle_request
                cmd.cmdloop()
            except Exception as e:
                raise e
                self.print_colored(str(e), ConsoleColors.FAIL)

    def handle_request(self, request):
        if request is not None:
            request_json = json.dumps(request)
            output_json = self.write_and_receive(request_json)
            if output_json:
                formatted = self.pretty_json(output_json)
                if formatted is not None:
                    print(formatted)
            else:
                self.print_colored("Response is malformed: '{0}'".format(output_json), ConsoleColors.FAIL)
        else:
            self.print_colored("Command is invalid", ConsoleColors.FAIL)

    def pretty_json(self, output_json):
        parsed = json.loads(output_json)
        if "response" in parsed:
            if parsed["type"] == PreferencesResource.RESOURCE:
                return self.preferences_resource.print_pretty(parsed["response"])
            elif parsed["type"] == SqliteResource.RESOURCE:
                return self.sqlite_resource.print_pretty(parsed["response"])
            else:
                return json.dumps(parsed["response"], indent=4, sort_keys=True)
        elif "error" in parsed:
            return "{1}{0}{2}".format(parsed["error"], ConsoleColors.FAIL, ConsoleColors.ENDC)

    def forward_port(self, port, device):
        portForward = "tcp:{0}".format(port)
        args = None
        if device is None:
            args = ["adb", "forward", portForward, portForward]
        else:
            args = ["adb", "-s", device, "forward", portForward, portForward]
        subprocess.run(args, stdout=subprocess.DEVNULL)

    def write_and_receive(self, data):
        port = self.obtain_port()
        self.forward_port(port, self.device)
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((self.HOST, port))

            encoded_value = self.encode_data(data)
            encoded_value = "{0}{1}".format(encoded_value, self.LINE_ENDING)
            s.send(bytes(encoded_value, self.ENCODING))

            encoded_response = ''
            while True:
                chunk = s.recv(1024)
                if not chunk or chunk[-1] == '\n':
                    break
                else:
                    encoded_response += chunk.decode()
            response = self.decode_data(encoded_response)
            
            s.close()
            return str(response, self.ENCODING).strip()

    def obtain_port(self):
        if self.port is None:
            self.port = self.PORT
        return int(self.port)

    def encode_data(self, data):
        data = data.encode(self.ENCODING)
        compressed = gzip.compress(data)
        return base64.b64encode(compressed).decode(self.ENCODING)
    
    def decode_data(self, data):
        data_bytes = base64.b64decode(data + "==")
        decompressed = gzip.decompress(data_bytes)
        return decompressed
    
    def print_colored(self, message, color, new_line = "\n"):
        print("{1}{0}{2}{3}".format(message, color, ConsoleColors.ENDC, new_line))

if sys.version_info[0] < 3:
    print("{1}{0}{2}\n".format("CARI supports only Python 3", ConsoleColors.FAIL, ConsoleColors.ENDC))
    exit()

client = CARIClient()
client.execute()