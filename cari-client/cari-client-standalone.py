#!/usr/bin/env python


import contextlib as __stickytape_contextlib

@__stickytape_contextlib.contextmanager
def __stickytape_temporary_dir():
    import tempfile
    import shutil
    dir_path = tempfile.mkdtemp()
    try:
        yield dir_path
    finally:
        shutil.rmtree(dir_path)

with __stickytape_temporary_dir() as __stickytape_working_dir:
    def __stickytape_write_module(path, contents):
        import os, os.path

        def make_package(path):
            parts = path.split("/")
            partial_path = __stickytape_working_dir
            for part in parts:
                partial_path = os.path.join(partial_path, part)
                if not os.path.exists(partial_path):
                    os.mkdir(partial_path)
                    open(os.path.join(partial_path, "__init__.py"), "w").write("\n")

        make_package(os.path.dirname(path))

        full_path = os.path.join(__stickytape_working_dir, path)
        with open(full_path, "w") as module_file:
            module_file.write(contents)

    import sys as __stickytape_sys
    __stickytape_sys.path.insert(0, __stickytape_working_dir)

    __stickytape_write_module('argumentshandler.py', 'import subprocess\nimport argparse\n\nclass ArgumentsHandler:\n\n    def count_connected_devices(self):\n        output = subprocess.check_output(["adb", "devices"])\n        lines = output.decode("UTF-8").strip().splitlines()\n        filtered = [line for line in lines if "\\tdevice" in line]\n        return len(filtered)\n\n    def process(self):\n        parser = argparse.ArgumentParser()\n        parser.add_argument(\'-d\', \'--device\', help="select device to use")\n        parser.add_argument(\'-p\', \'--port\', help="select adb port to forward")\n        args = parser.parse_args()\n\n        devices_count = self.count_connected_devices()\n        if devices_count > 0:\n            if devices_count > 1 and args.device is None:\n                raise Exception("Please specify device to use")\n            port = args.port\n            device = args.device\n            return port, device\n        else:\n            raise Exception("No connected devices")')
    __stickytape_write_module('preferencesresource.py', 'from consolecolors import ConsoleColors\n\nclass PreferencesResource:\n\n    RESOURCE = "prefs"\n    TAB_SIZE = 4\n\n    def handle_resource(self, args):\n        args_count = len(args)\n        command = args[1]\n        request = None\n        \n        if (args_count == 2 or args_count == 3) and command == "dump":\n            arguments = []\n            if args_count == 3:\n                scope = args[2]\n                arguments.append(scope)\n            request = self.create_command_prefs(command, arguments)\n\n        if args_count == 2 and command == "scopes":\n            request = self.create_command_prefs(command)\n            \n        if args_count == 3 and (command == "list"):\n            scope = args[2]\n            arguments = [scope]\n            request = self.create_command_prefs(command, arguments)\n\n        if args_count == 4 and command == "get":\n            scope = args[2]\n            key = args[3]\n            arguments = [scope, key]\n            request = self.create_command_prefs(command, arguments)\n\n        if args_count == 5 and command == "set":\n            scope = args[2]\n            key = args[3]\n            value = args[4]\n            arguments = [scope, key, value]\n            request = self.create_command_prefs(command, arguments)\n\n        if args_count == 4 and (command == "remove"):\n            scope = args[2]\n            key = args[3]\n            arguments = [scope, key]\n            request = self.create_command_prefs(command, arguments)\n\n        return request\n\n    def create_command_prefs(self, command, arguments = []):\n        data = {\n            "resource": self.RESOURCE,\n            "command": command,\n            "arguments": arguments\n        }\n        return data\n    \n    def print_pretty(self, response):\n        if isinstance(response, list):\n            self.print_pretty_list(response)\n        elif isinstance(response, dict):\n            self.print_pretty_dict(response)\n        else:\n            print("{1}{0}{2}\\n".format(response, ConsoleColors.BOLD, ConsoleColors.ENDC))\n\n    def print_pretty_list(self, response):\n        for item in response:\n            print("{1}{0}{2}".format(item, ConsoleColors.BOLD, ConsoleColors.ENDC))\n        print("\\n")\n    \n    def print_pretty_dict(self, response):\n        for item in response:\n            value = response[item]\n            if isinstance(value, dict):\n                print("{1}{0}{2}".format(item, ConsoleColors.HEADER, ConsoleColors.ENDC))\n                for sub_item in value:\n                    sum_value = value[sub_item]\n                    print("\\t{2}{0}{3}: {1}".format(sub_item, sum_value, ConsoleColors.BOLD, ConsoleColors.ENDC).expandtabs(self.TAB_SIZE))\n            else:\n                print("{2}{0}{3}: {1}".format(item, value, ConsoleColors.BOLD, ConsoleColors.ENDC))\n        print("\\n")')
    __stickytape_write_module('consolecolors.py', "class ConsoleColors:\n    HEADER = '\\033[95m'\n    OKBLUE = '\\033[94m'\n    OKGREEN = '\\033[92m'\n    WARNING = '\\033[93m'\n    FAIL = '\\033[91m'\n    ENDC = '\\033[0m'\n    BOLD = '\\033[1m'\n    UNDERLINE = '\\033[4m'")
    __stickytape_write_module('sqliteresource.py', 'from consolecolors import ConsoleColors\nimport ast\n\nclass SqliteResource:\n\n    RESOURCE = "sqlite"\n    TAB_SIZE = 4\n\n    def handle_resource(self, args):\n        args_count = len(args)\n        command = args[1]\n        request = None\n        \n        if args_count == 2 and command == "databases":\n            request = self.create_command_sqlite(command)\n        \n        if args_count == 3 and command == "tables":\n            database = args[2]\n            arguments = [database]\n            request = self.create_command_sqlite(command, arguments)\n\n        if args_count > 2 and command == "q":\n            database = args[2]\n            arguments = [database]\n            arguments.append(" ".join(args[3:]))\n            request = self.create_command_sqlite(command, arguments)\n        \n        return request\n\n    def create_command_sqlite(self, command, arguments = []):\n        data = {\n            "resource": self.RESOURCE,\n            "command": command,\n            "arguments": arguments\n        }\n        return data\n\n    def print_pretty(self, response):\n        if isinstance(response, list):\n            self.print_pretty_list(response)\n        elif isinstance(response, dict):\n            if "result" in response and len(response["result"]) > 0:\n                self.print_query_result(response["result"])\n            if "limitedRows" in response and int(response["limitedRows"]) > 0:\n                print("Result is limited, there\'s {0} more rows.".format(response["limitedRows"]))\n            print(response["summary"])\n            print("\\n")\n        else:\n            print("{1}{0}{2}\\n".format(response, ConsoleColors.BOLD, ConsoleColors.ENDC))\n\n    def print_pretty_list(self, response):\n        for item in response:\n            print("{1}{0}{2}".format(item, ConsoleColors.BOLD, ConsoleColors.ENDC))\n        print("\\n")\n    \n    def print_query_result(self, result):\n        columns = result[0]\n        rows = result[1:]\n\n        column_min_width = 15\n        columns_width = []\n\n        for column in columns:\n            columns_width.append(max(column_min_width, len(column)))\n\n        dot_row = self.create_dot_row(columns, columns_width)\n        print(dot_row)\n\n        self.print_column_names(columns, columns_width)\n        print(dot_row)\n\n        self.print_rows(rows, columns_width)\n        print(dot_row)\n\n    def create_dot_row(self, columns, columns_width):\n        dot_row = ""\n        for index, column in enumerate(columns):\n            column_width = columns_width[index]\n            data = "| {0: <{width}} ".format("", width=column_width)\n            dot_row += "+"\n            for i in range(1, len(data)):\n                dot_row += "-"\n        dot_row += "+"\n        return dot_row\n\n    def print_column_names(self, columns, columns_width):\n        columns_row = ""\n        for index, column in enumerate(columns):\n            column_width = columns_width[index]\n            columns_row += "| {name: <{width}} ".format(name=column, width=column_width)\n        print(columns_row + "|")\n\n    def print_rows(self, rows, columns_width):\n        for row in rows:\n            row_string = ""\n            for index, value in enumerate(row):\n                column_width = columns_width[index]\n                if len(value) > column_width - 3:\n                    value = value[:column_width - 6] + "..."\n                row_string += "| {value: <{width}} ".format(value=value, width=column_width)\n            print(row_string + "|")')
    __stickytape_write_module('cmdprompt.py', 'from cmd import Cmd\nfrom preferencesresource import PreferencesResource\nfrom sqliteresource import SqliteResource\nfrom consolecolors import ConsoleColors\n\nclass CmdPrompt(Cmd):\n\n    prompt = "CARI$ "\n    use = None\n    prefs_scope = None\n    sqlite_active_db = None\n\n    preferences_resource = PreferencesResource()\n    sqlite_resource = SqliteResource()\n    resources = [preferences_resource, sqlite_resource]\n\n    request_callback = None\n\n    def do_exit(self, inp):\n        return True\n\n    #\n    # PREFS\n    # can be used with only "use"\n    def do_dump(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            args = self.create_prefs_args()\n            args.insert(1, "dump")\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    # can be used with only "use"\n    def do_scopes(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            args = self.create_prefs_args()\n            args.append("scopes")\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    # can be used with "use" and "scope"\n    def do_ls(self, arg):\n        self.do_list(arg)\n    def do_list(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            args = self.create_prefs_args()\n            args.insert(1, "list")\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def do_get(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            args = self.create_prefs_args()\n            args.insert(1, "get")\n            args.append(arg)\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def do_rm(self, arg):\n        self.do_remove(arg)\n    def do_remove(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            args = self.create_prefs_args()\n            args.insert(1, "remove")\n            args.append(arg)\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def do_set(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            args = self.create_prefs_args()\n            args.insert(1, "set")\n            args.extend(arg.split())\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def create_prefs_args(self):\n        args = []\n        if self.use is not None:\n            args.append(self.use)\n        if self.prefs_scope is not None:\n            args.append(self.prefs_scope)\n        return args\n    # /PREFS\n    #\n\n\n    #\n    # SQLITE\n    # can be used with only "use"\n    def default(self, arg):\n        if self.use == SqliteResource.RESOURCE and self.sqlite_active_db is not None:\n            self.do_q(arg)\n        else:\n            if self.use is None:\n                self.print_available_scopes()\n            elif self.use == SqliteResource.RESOURCE:\n                print("Available commands: use, databases")\n            elif self.use == PreferencesResource.RESOURCE:\n                print("Available commands: use, dump, scopes")\n            else:\n                print("Unknown command")\n\n    def do_databases(self, arg):\n        if self.use == SqliteResource.RESOURCE:\n            args = self.create_sqlite_args()\n            args.insert(1, "databases")\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def do_tables(self, arg):\n        if self.use == SqliteResource.RESOURCE:\n            if self.sqlite_active_db is None:\n                self.print_colored("Please select a database", ConsoleColors.WARNING)\n                return\n            args = self.create_sqlite_args()\n            args.insert(1, "tables")\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def do_q(self, arg):\n        if self.use == SqliteResource.RESOURCE:\n            if self.sqlite_active_db is None:\n                self.print_colored("Please select a database", ConsoleColors.WARNING)\n                return\n            args = self.create_sqlite_args()\n            args.insert(1, "q")\n            args.extend(arg.split())\n            request = self.handle_resource(args)\n            self.request_callback(request)\n        else:\n            print("Please use correct resource")\n\n    def create_sqlite_args(self):\n        args = []\n        if self.use is not None:\n            args.append(self.use)\n        if self.sqlite_active_db is not None:\n            args.append(self.sqlite_active_db)\n        return args\n    # /SQLITE\n    #\n\n    def do_use(self, arg):\n        if self.use == PreferencesResource.RESOURCE:\n            self.prefs_scope = arg\n            self.prompt = "CARI ({0}\\{1})$ ".format(self.use, arg)\n        elif self.use == SqliteResource.RESOURCE:\n            self.sqlite_active_db = arg\n            self.prompt = "CARI ({0}\\{1})$ ".format(self.use, arg)\n        else:\n            resources_names = [resource.RESOURCE for resource in self.resources]\n            if arg in resources_names:\n                self.use = arg\n                self.prompt = "CARI ({0})$ ".format(arg)\n            else:\n                self.print_available_scopes()\n\n    def print_available_scopes(self):\n        self.print_colored("Unknown resource", ConsoleColors.WARNING)\n        print("Available resources:")\n        resources_names = [resource.RESOURCE for resource in self.resources]\n        for name in resources_names:\n            print("{1}\\t{0}{2}".format(name, ConsoleColors.HEADER, ConsoleColors.ENDC).expandtabs(4))\n\n    def do_clear(self, arg):\n        self.use = None\n        self.prefs_scope = None\n        self.sqlite_active_db = None\n        self.prompt = "CARI$ "\n\n    def do_version(self, arg):\n        self.request_callback("version")\n        \n    def handle_resource(self, action):\n        resource = action[0]\n        if resource == PreferencesResource.RESOURCE:\n            return self.preferences_resource.handle_resource(action)\n        elif resource == SqliteResource.RESOURCE:\n            return self.sqlite_resource.handle_resource(action)\n\n    def print_colored(self, message, color):\n        print("{1}{0}{2}\\n".format(message, color, ConsoleColors.ENDC))\n')
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
        VERSION = '1.0.0'
    
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