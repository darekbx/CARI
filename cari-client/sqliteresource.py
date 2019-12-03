
class SqliteResource:

    RESOURCE = "sqlite"

    def handle_resource(self, args):
        args_count = len(args)
        command = args[0]
        request = None

        return request