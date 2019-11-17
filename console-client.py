#!/usr/bin/env python3
import socket
import subprocess
import base64
import gzip


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

    def forward_port(self):
        portForward = "tcp:{0}".format(self.PORT)
        subprocess.run(["adb", "forward", portForward, portForward])

    def write_and_receive(self, command):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((self.HOST, self.PORT))

            encoded_value = self.encode_data(command)
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
output = client.write_and_receive("Execute command")
print(output)