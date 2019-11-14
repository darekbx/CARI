import socket

# adb forward tcp:38300 tcp:38300
# adb forward --remove-all
# lsof -i :38300

HOST = '127.0.0.1'  # Standard loopback interface address (localhost)
PORT = 38300        # Port to listen on (non-privileged ports are > 1023)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    data = s.recv(1024)
    s.send(b'pc_data')
    #data = s.recv(1024)
    s.close()
    print(data)
