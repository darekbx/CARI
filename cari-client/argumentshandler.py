import subprocess
import argparse

from cariexception import CARIException

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
                raise CARIException("Please specify device to use")
            port = args.port
            device = args.device
            return port, device
        else:
            raise CARIException("No connected devices")