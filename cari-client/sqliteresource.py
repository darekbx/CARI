from consolecolors import ConsoleColors

class SqliteResource:

    RESOURCE = "sqlite"
    TAB_SIZE = 4

    def handle_resource(self, args):
        args_count = len(args)
        command = args[1]
        request = None
        
        if args_count == 2 and command == "databases":
            request = self.create_command_sqlite(command)
        
        if args_count == 3 and command == "tables":
            database = args[2]
            arguments = [database]
            request = self.create_command_sqlite(command, arguments)

        if args_count > 2 and command == "q":
            database = args[2]
            arguments = [database]
            arguments.append(" ".join(args[3:]))
            request = self.create_command_sqlite(command, arguments)
        
        return request

    def create_command_sqlite(self, command, arguments = []):
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
            print(response["result"])
            print(response["summary"])
        else:
            print("{1}{0}{2}\n".format(response, ConsoleColors.BOLD, ConsoleColors.ENDC))

    def print_pretty_list(self, response):
        for item in response:
            print("{1}{0}{2}".format(item, ConsoleColors.BOLD, ConsoleColors.ENDC))
        print("\n")