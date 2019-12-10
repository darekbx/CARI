from consolecolors import ConsoleColors

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
    
    def print_pretty(self, response):
        if isinstance(response, list):
            for item in response:
                print("{1}{0}{2}".format(item, ConsoleColors.BOLD, ConsoleColors.ENDC))
        elif isinstance(response, dict):
            for item in response:
                value = response[item]
                if isinstance(value, dict):
                    print("{1}{0}{2}".format(item, ConsoleColors.HEADER, ConsoleColors.ENDC))
                    for sub_item in value:
                        sum_value = value[sub_item]
                        print("\t{2}{0}{3}: {1}".format(sub_item, sum_value, ConsoleColors.BOLD, ConsoleColors.ENDC))
                    print("\n")
                else:
                    print("{2}{0}{3}: {1}".format(item, value, ConsoleColors.BOLD, ConsoleColors.ENDC))
        else:
            print("{1}{0}{2}".format(response, ConsoleColors.BOLD, ConsoleColors.ENDC))
        print("\n")