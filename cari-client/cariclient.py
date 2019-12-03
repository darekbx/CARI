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

'''
CARI (Console Android Resources Inspector)
Client used to communicate with Android device


Instruction:

# TODO: add to android sdk

Connect Android device to the computer and run: python3 cariclient.py

Options:
-d provide a device
-p provide custom port for forward, default is 38300

Shell commands:
use {resource/prefs_scope}  - Use resource, scope for further actions
clear                       - Clear used resource and scopes 
version                     - Print CARI Android SDK version

Resources:
    - prefs
        Android shared preferences wrapper, commands:
            - dump - print all preferences data (can be used with scope)
            - list - list all key
            - remove - remove key with value
            - set - set value to key
            - get - get key value
    - sqlite


How to list keys from preferences scope:
1. Run CARI: python3 cariclient.py
2. In CARI Shell type: use prefs
3. Dump all scopes, by typing in shell: dump
4. Use preferences scope (eg app_preferences), by typing in shell: use app_preferences   
5. Type in shell to list all keys: list


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
    port = None
    device = None

    def __init__(self):
        print("CARI Console Android Resource Inspector, v{0}".format(self.VERSION))
        request = PreferencesResource().handle_resource([PreferencesResource.RESOURCE, "version"])
        request_json = json.dumps(request)
        result = self.write_and_receive(request_json, self.port)
        result_array = json.loads(result)
        print(" ".join(result_array))

    def execute(self):
        try:
            self.port, self.device = self.arguments_handler.process()
            cmd = CmdPrompt()
            cmd.request_callback = self.handle_request
            cmd.cmdloop()
        except Exception as e:
            print(e)

    def handle_request(self, request):

        # TODO adb forward with device?

        if request is not None:
            request_json = json.dumps(request)
            output_json = self.write_and_receive(request_json, self.port)
            if output_json:
                formatted = self.pretty_json(output_json)
                print(formatted)
            else:
                print("Response is malformed: '{0}'".format(output_json))
        else:
            print("Command is invalid")

    def pretty_json(self, output_json):
        parsed = json.loads(output_json)
        return json.dumps(parsed, indent=4, sort_keys=True)

    def forward_port(self, port):
        portForward = "tcp:{0}".format(port)
        subprocess.run(["adb", "forward", portForward, portForward])

    def write_and_receive(self, data, port):
        port = self.obtain_port()
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


client = CARIClient()
client.execute()