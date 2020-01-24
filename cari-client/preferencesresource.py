from consolecolors import ConsoleColors

class PreferencesResource:

    RESOURCE = "prefs"
    TAB_SIZE = 4

    def handle_resource(self, args):
        args_count = len(args)
        command = args[1]
        request = None
        
        if (args_count == 2 or args_count == 3) and command == "dump":
            arguments = []
            if args_count == 3:
                scope = args[2]
                arguments.append(scope)
            request = self.create_command_prefs(command, arguments)

        if args_count == 2 and command == "scopes":
            request = self.create_command_prefs(command)
            
        if args_count == 3 and (command == "list"):
            scope = args[2]
            arguments = [scope]
            request = self.create_command_prefs(command, arguments)

        if args_count == 4 and command == "get":
            scope = args[2]
            key = args[3]
            arguments = [scope, key]
            request = self.create_command_prefs(command, arguments)

        if args_count == 5 and command == "set":
            scope = args[2]
            key = args[3]
            value = args[4]
            arguments = [scope, key, value]
            request = self.create_command_prefs(command, arguments)

        if args_count == 4 and (command == "remove"):
            scope = args[2]
            key = args[3]
            arguments = [scope, key]
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
            self.print_pretty_list(response)
        elif isinstance(response, dict):
            self.print_pretty_dict(response)
        else:
            print("{1}{0}{2}\n".format(response, ConsoleColors.OKGREEN, ConsoleColors.ENDC))

    def print_pretty_list(self, response):
        for item in response:
            print("{1}{0}{2}".format(item, ConsoleColors.OKGREEN, ConsoleColors.ENDC))
        print("\n")
    
    def print_pretty_dict(self, response):
        for item in response:
            value = response[item]
            if isinstance(value, dict):
                print("{1}{0}{2}".format(item, ConsoleColors.HEADER, ConsoleColors.ENDC))
                for sub_item in value:
                    sum_value = value[sub_item]
                    print("\t{2}{0}{3}: {1}".format(sub_item, sum_value, ConsoleColors.OKGREEN, ConsoleColors.ENDC).expandtabs(self.TAB_SIZE))
            else:
                print("{2}{0}{3}: {1}".format(item, value, ConsoleColors.OKGREEN, ConsoleColors.ENDC))
        print("\n")