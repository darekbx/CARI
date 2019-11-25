#!/usr/bin/env python3
import sys
import socket
import subprocess
import base64
import gzip
import json

'''
CARI
Console
Android
Resources
Inspector

Client used to communicate with Android device


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
        command = args[0]
        request = None
        if args_count == 1 and command == "dump":
            request = self.create_command_prefs(command)

        if args_count == 1 and command == "scopes":
            request = self.create_command_prefs(command)

        if args_count == 2 and command == "list":
            scope = args[1]
            arguments = [{"option":scope}]
            request = self.create_command_prefs(command, arguments)

        if args_count == 3 and command == "get":
            scope = args[1]
            key = args[2]
            arguments = [{"option":scope},{"option":key}]
            request = self.create_command_prefs(command, arguments)

        if args_count == 4 and command == "set":
            scope = args[1]
            key = args[2]
            value = args[3]
            arguments = [{"option":scope},{"option":key},{"option":value}]
            request = self.create_command_prefs(command, arguments)

        if args_count == 3 and command == "remove":
            scope = args[1]
            key = args[2]
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

    PORT_ARGUMENT = "--port"

    preferences_resource = PreferencesResource()
    sqlite_resource = SqliteResource()

    def count_connected_devices(self):
        output = subprocess.check_output(["adb", "devices"])
        lines = output.decode("UTF-8").strip().splitlines()
        filtered = [line for line in lines if "\tdevice" in line]
        return len(filtered)

    def process(self):
        args_count = len(sys.argv)
        if self.count_connected_devices() == 1:
            port = None
            arguments_offset = 1
            if args_count > 1:

                if self.PORT_ARGUMENT in sys.argv[1] and args_count > 3:
                    arguments_offset = 3
                    port = sys.argv[2]

                if args_count > arguments_offset + 1:
                    resource = sys.argv[arguments_offset]
                    request = self.handle_resource(resource, arguments_offset)
                    return port, request
                else:
                    print("Unknown command, run with -h or --help for more information")
            else:
                print("Unknown command, run with -h or --help for more information")
        else:
            print("No connected devices, or connected too many devices")

    def handle_resource(self, resource, arguments_offset):
        if resource == PreferencesResource.RESOURCE:
            args = sys.argv[(arguments_offset + 1):]
            return self.preferences_resource.handle_resource(args)
        elif resource == SqliteResource.RESOURCE:
            args = sys.argv[(arguments_offset + 1):]
            return self.sqlite_resource.handle_resource(args)


class CARIClient:

    # adb forward tcp:38300 tcp:38300
    # adb forward --remove-all
    # lsof -i :38300

    HOST = '127.0.0.1'
    PORT = 38300

    ENCODING = "UTF-8"
    LINE_ENDING = "\r\n"

    arguments_handler = ArgumentsHandler()

    def __init__(self):
        print("CARI Console Android Resource Inspector")

    def execute(self):
        port, request = self.arguments_handler.process()

        if port is None:
            port = self.PORT

        request_json = json.dumps(request)
        output_json = self.write_and_receive(request_json, port)
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