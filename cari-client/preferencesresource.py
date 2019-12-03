class PreferencesResource:

    RESOURCE = "prefs"

    def handle_resource(self, args):
        args_count = len(args)
        command = args[1]
        request = None
        
        if args_count == 2 and command == "version":
            request = self.create_command_prefs(command)

        if (args_count == 2 or args_count == 3) and command == "dump":
            arguments = []
            if args_count == 3:
                scope = args[2]
                arguments.append({"option":scope})
            request = self.create_command_prefs(command, arguments)

        if args_count == 2 and command == "scopes":
            request = self.create_command_prefs(command)
            
        if args_count == 3 and (command == "list"):
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

        if args_count == 4 and (command == "remove"):
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