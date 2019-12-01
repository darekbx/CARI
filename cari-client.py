#!/usr/bin/env python3
import sys
import socket
import subprocess
import base64
import gzip
import json
import argparse
from cmd import Cmd

'''
CARI
Console
Android
Resources
Inspector

Client used to communicate with Android device


Instruction:
Run: python3 cari-client.py

Options:
-d provide device for use
-p provide custom port for forward, default is 38300
-a provide action 
Dump preferences: python3 cari-client.py -a prefs dump
                  python3 cari-client.py -d device -a prefs dump
                  python3 cari-client.py -d device -p 4000 -a prefs dump


TODO:
handle connection errors
'''


class SqliteResource:

    RESOURCE = "sqlite"

    def handle_resource(self, args):
        args_count = len(args)
        command = args[0]
        request = None

        return request

class PreferencesResource:

    RESOURCE = "prefs"

    def handle_resource(self, args):
        args_count = len(args)
        command = args[1]
        request = None
        if args_count == 2 and command == "dump":
            request = self.create_command_prefs(command)

        if args_count == 2 and command == "scopes":
            request = self.create_command_prefs(command)

        if args_count == 3 and (command == "list" or command == "ls"):
            scope = args[2]
            arguments = [{"option":scope}]
            request = self.create_command_prefs(command, arguments)

        if args_count == 4 and command == "get":
            scope = args[2]
            key = args[3]
            arguments = [{"option":scope},{"option":key}]
            request = self.create_command_prefs(command, arguments)

        if args_count == 5 and command == "set":
            scope = args[2]
            key = args[3]
            value = args[4]
            arguments = [{"option":scope},{"option":key},{"option":value}]
            request = self.create_command_prefs(command, arguments)

        if args_count == 4 and (command == "remove" or command == "rm"):
            scope = args[2]
            key = args[3]
            arguments = [{"option":scope},{"option":key}]
            request = self.create_command_prefs(command, arguments)

        return request

    def create_command_prefs(self, command, arguments = []):
        data = {
            "resource": self.RESOURCE,
            "command": command,
            "arguments": arguments
        }
        return data

class ArgumentsHandler:

    def count_connected_devices(self):
        output = subprocess.check_output(["adb", "devices"])
        lines = output.decode("UTF-8").strip().splitlines()
        filtered = [line for line in lines if "\tdevice" in line]
        return len(filtered)

    def process(self):
        parser = argparse.ArgumentParser()
        parser.add_argument('-d', '--device', help="select device to use")
        parser.add_argument('-p', '--port', help="select adb port to forward")
        args = parser.parse_args()

        devices_count = self.count_connected_devices()
        if devices_count > 0:
            if devices_count > 1 and args.device is None:
                print("Please specify device to use")
            port = args.port
            device = args.device
            return port, device
        else:
            raise Exception("No connected devices")
 
class CARIPrompt(Cmd):

    prompt = "CARI$ "

    preferences_resource = PreferencesResource()
    sqlite_resource = SqliteResource()

    request_callback = None

    def do_exit(self, inp):
        return True

    def do_prefs(self, arg):
        args = arg.split()
        args.insert(0, PreferencesResource.RESOURCE)
        request = self.handle_resource(args)
        self.request_callback(request)

    def handle_resource(self, action):
        resource = action[0]
        if resource == PreferencesResource.RESOURCE:
            return self.preferences_resource.handle_resource(action)
        elif resource == SqliteResource.RESOURCE:
            return self.sqlite_resource.handle_resource(action)

class CARIClient:

    # adb forward tcp:38300 tcp:38300
    # adb forward --remove-all
    # lsof -i :38300

    HOST = '127.0.0.1'
    PORT = 38300

    ENCODING = "UTF-8"
    LINE_ENDING = "\r\n"

    arguments_handler = ArgumentsHandler()
    port = None
    device = None

    def __init__(self):
        print("CARI Console Android Resource Inspector")

    def execute(self):
        try:
            self.port, self.device = self.arguments_handler.process()
            # TODO: run cmd prompt for further commands
            cmd = CARIPrompt()
            cmd.request_callback = self.handle_request
            cmd.cmdloop()
        except Exception as e:
            print(e)    

    def prefs_callback(self, request):
        print("A {0}".format(request))

    def handle_request(self, request):
        if self.port is None:
            self.port = self.PORT

        # TODO adb forward with device?

        request_json = json.dumps(request)
        output_json = self.write_and_receive(request_json, self.port)
        if output_json:
            formatted = self.pretty_json(output_json)
            print(formatted)
        else:
            print("Response is malformed: '{0}'".format(output_json))

    def pretty_json(self, output_json):
        parsed = json.loads(output_json)
        return json.dumps(parsed, indent=4, sort_keys=True)

    def forward_port(self, port):
        portForward = "tcp:{0}".format(port)
        subprocess.run(["adb", "forward", portForward, portForward])

    def write_and_receive(self, data, port):
        port = int(port)
        self.forward_port(port)
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((self.HOST, port))

            encoded_value = self.encode_data(data)
            encoded_value = "{0}{1}".format(encoded_value, self.LINE_ENDING)
            s.send(bytes(encoded_value, self.ENCODING))

            encoded_response = s.recv(1024)
            response = self.decode_data(encoded_response)
            
            s.close()
            return str(response, self.ENCODING).strip()

    def encode_data(self, data):
        data = data.encode(self.ENCODING)
        compressed = gzip.compress(data)
        return base64.b64encode(compressed).decode(self.ENCODING)
    
    def decode_data(self, data):
        data_bytes = base64.b64decode(data.decode(self.ENCODING))
        decompressed = gzip.decompress(data_bytes)
        return decompressed


client = CARIClient()
client.execute()

