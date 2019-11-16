#!/usr/bin/env python3
import socket
import subprocess

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

    def write_and_receive(self, value):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            value = "{0}{1}".format(value, self.LINE_ENDING)
            s.connect((self.HOST, self.PORT))
            s.send(bytes(value, self.ENCODING))
            data = s.recv(1024)
            s.close()
            return str(data, self.ENCODING).strip()

client = CARIClient()
output = client.write_and_receive("Execute command")
print(output)