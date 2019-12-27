from consolecolors import ConsoleColors
import ast

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
            if "result" in response and len(response["result"]) > 0:
                self.print_query_result(response["result"])
            print(response["summary"])
            print("\n")
        else:
            print("{1}{0}{2}\n".format(response, ConsoleColors.BOLD, ConsoleColors.ENDC))

    def print_pretty_list(self, response):
        for item in response:
            print("{1}{0}{2}".format(item, ConsoleColors.BOLD, ConsoleColors.ENDC))
        print("\n")
    
    def print_query_result(self, result):
        columns = result[0]
        rows = result[1:]

        column_min_width = 15
        columns_width = []

        for column in columns:
            columns_width.append(max(column_min_width, len(column)))

        dot_row = self.create_dot_row(columns, columns_width)
        print(dot_row)

        self.print_column_names(columns, columns_width)
        print(dot_row)

        self.print_rows(rows, columns_width)
        print(dot_row)

    def create_dot_row(self, columns, columns_width):
        dot_row = ""
        for index, column in enumerate(columns):
            column_width = columns_width[index]
            data = "| {0: <{width}} ".format("", width=column_width)
            for i in range(0, len(data)):
                dot_row += "-"
        dot_row += "-"
        return dot_row

    def print_column_names(self, columns, columns_width):
        columns_row = ""
        for index, column in enumerate(columns):
            column_width = columns_width[index]
            columns_row += "| {name: <{width}} ".format(name=column, width=column_width)
        print(columns_row + "|")

    def print_rows(self, rows, columns_width):
        for row in rows:
            row_string = ""
            for index, value in enumerate(row):
                column_width = columns_width[index]
                if len(value) > column_width - 3:
                    value = value[:column_width - 6] + "..."
                row_string += "| {value: <{width}} ".format(value=value, width=column_width)
            print(row_string + "|")