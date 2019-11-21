#!/usr/bin/env python3
import sys
import socket
import subprocess
import base64
import gzip
import json


'''
Console
Android
Resources
Inspector

Client used to communicate with Android device


TODO:
ability to set port from parameters
check if device is available
handle connection errors

'''
class CARIClient:

    # adb forward tcp:38300 tcp:38300
    # adb forward --remove-all
    # lsof -i :38300

    HOST = '127.0.0.1'
    PORT = 38300

    ENCODING = "UTF-8"
    LINE_ENDING = "\r\n"

    def __init__(self):
        self.forward_port()
        print("CARI Console Android Resource Inspector")


    def execute(self):
        result = ""
        args_count = len(sys.argv)
        if args_count > 1:
            resource = sys.argv[1]

            if resource == "prefs":
                result = self.handle_prefs()

        print(result)

    def handle_prefs(self):
        args_count = len(sys.argv)
        resource = "prefs"
        command = sys.argv[2]
        if args_count == 3 and command == "scopes":
            data = self.create_command_prefs(resource, command)

        if args_count == 4 and command == "list":
            scope = sys.argv[3]
            arguments = [{"option":scope}]
            data = self.create_command_prefs(resource, command, arguments)

        if args_count == 5 and command == "get":
            scope = sys.argv[3]
            key = sys.argv[4]
            arguments = [{"option":scope},{"option":key}]
            data = self.create_command_prefs(resource, command, arguments)

        if args_count == 6 and command == "set":
            scope = sys.argv[3]
            key = sys.argv[4]
            value = sys.argv[5]
            arguments = [{"option":scope},{"option":key},{"option":value}]
            data = self.create_command_prefs(resource, command, arguments)

        if args_count == 5 and command == "remove":
            scope = sys.argv[3]
            key = sys.argv[4]
            arguments = [{"option":scope},{"option":key}]
            data = self.create_command_prefs(resource, command, arguments)

        data_json = json.dumps(data)
        return self.write_and_receive(data_json)

    def create_command_prefs(self, resource, command, arguments = []):
        data = {
            "resource": resource,
            "command": command,
            "arguments": arguments
        }
        return data

    def forward_port(self):
        portForward = "tcp:{0}".format(self.PORT)
        subprocess.run(["adb", "forward", portForward, portForward])

    def write_and_receive(self, data):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((self.HOST, self.PORT))

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