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
from cmdprompt import CmdPrompt
from consolecolors import ConsoleColors

'''
CARI (Console Android Resources Inspector)
Client used to communicate with Android device
'''
class CARIClient:

    # adb forward tcp:38300 tcp:38300
    # adb forward --remove-all
    # lsof -i :38300

    HOST = '127.0.0.1'
    PORT = 38300
    VERSION = '0.1.0'

    ENCODING = "UTF-8"
    LINE_ENDING = "\r\n"

    arguments_handler = ArgumentsHandler()
    preferences_resource = PreferencesResource()
    port = None
    device = None
    initialized = False

    def __init__(self):
        print("CARI Console Android Resource Inspector, v{0}".format(self.VERSION))
        try:
            self.port, self.device = self.arguments_handler.process()
            self.check_status()
            self.initialized = True
        except:
            self.print_colored("Device with SDK is unavailable", ConsoleColors.FAIL)

    def check_status(self):
        version_request = "version"
        result = self.write_and_receive(version_request)
        result_array = json.loads(result)
        self.print_colored(" ".join(result_array["response"]), ConsoleColors.OKGREEN)

    def execute(self):
        if self.initialized:
            try:
                cmd = CmdPrompt()
                cmd.request_callback = self.handle_request
                cmd.cmdloop()
            except Exception as e:
                #raise e
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
        if parsed["response"]:
            if parsed["type"] == PreferencesResource.RESOURCE:
                return self.preferences_resource.print_pretty(parsed["response"])
            else:
                return json.dumps(parsed["response"], indent=4, sort_keys=True)

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

            encoded_response = s.recv(1024)
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
        data_bytes = base64.b64decode(data.decode(self.ENCODING))
        decompressed = gzip.decompress(data_bytes)
        return decompressed
    
    def print_colored(self, message, color):
        print("{1}{0}{2}\n".format(message, color, ConsoleColors.ENDC))

client = CARIClient()
client.execute()