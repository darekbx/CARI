from cmd import Cmd
from preferencesresource import PreferencesResource
from sqliteresource import SqliteResource
from consolecolors import ConsoleColors

class CmdPrompt(Cmd):

    prompt = "CARI$ "
    use = None
    prefs_scope = None
    sqlite_active_db = None

    preferences_resource = PreferencesResource()
    sqlite_resource = SqliteResource()
    resources = [preferences_resource, sqlite_resource]

    request_callback = None

    def do_exit(self, inp):
        return True

    #
    # PREFS
    # can be used with only "use"
    def do_dump(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "dump")
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    # can be used with only "use"
    def do_scopes(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.append("scopes")
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    # can be used with "use" and "scope"
    def do_ls(self, arg):
        self.do_list(arg)
    def do_list(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "list")
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def do_get(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "get")
            args.append(arg)
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def do_rm(self, arg):
        self.do_remove(arg)
    def do_remove(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "remove")
            args.append(arg)
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def do_set(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "set")
            args.extend(arg.split())
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def create_prefs_args(self):
        args = []
        if self.use is not None:
            args.append(self.use)
        if self.prefs_scope is not None:
            args.append(self.prefs_scope)
        return args
    # /PREFS
    #


    #
    # SQLITE
    # can be used with only "use"
    def default(self, arg):
        if self.use == SqliteResource.RESOURCE and self.sqlite_active_db is not None:
            self.do_q(arg)
        else:
            if self.use is None:
                self.print_available_scopes()
            elif self.use == SqliteResource.RESOURCE:
                print("Available commands: use, databases")
            elif self.use == PreferencesResource.RESOURCE:
                print("Available commands: use, dump, scopes")
            else:
                print("Unknown command")

    def do_databases(self, arg):
        if self.use == SqliteResource.RESOURCE:
            args = self.create_sqlite_args()
            args.insert(1, "databases")
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def do_tables(self, arg):
        if self.use == SqliteResource.RESOURCE:
            if self.sqlite_active_db is None:
                self.print_colored("Please select a database", ConsoleColors.WARNING)
                return
            args = self.create_sqlite_args()
            args.insert(1, "tables")
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def do_q(self, arg):
        if self.use == SqliteResource.RESOURCE:
            if self.sqlite_active_db is None:
                self.print_colored("Please select a database", ConsoleColors.WARNING)
                return
            args = self.create_sqlite_args()
            args.insert(1, "q")
            args.extend(arg.split())
            request = self.handle_resource(args)
            self.request_callback(request)
        else:
            print("Please use correct resource")

    def create_sqlite_args(self):
        args = []
        if self.use is not None:
            args.append(self.use)
        if self.sqlite_active_db is not None:
            args.append(self.sqlite_active_db)
        return args
    # /SQLITE
    #

    def do_use(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            self.prefs_scope = arg
            self.prompt = "CARI ({0}\{1})$ ".format(self.use, arg)
        elif self.use == SqliteResource.RESOURCE:
            self.sqlite_active_db = arg
            self.prompt = "CARI ({0}\{1})$ ".format(self.use, arg)
        else:
            resources_names = [resource.RESOURCE for resource in self.resources]
            if arg in resources_names:
                self.use = arg
                self.prompt = "CARI ({0})$ ".format(arg)
            else:
                self.print_available_scopes()

    def print_available_scopes(self):
        self.print_colored("Unknown resource", ConsoleColors.WARNING)
        print("Available resources:")
        resources_names = [resource.RESOURCE for resource in self.resources]
        for name in resources_names:
            print("{1}\t{0}{2}".format(name, ConsoleColors.HEADER, ConsoleColors.ENDC).expandtabs(4))

    def do_clear(self, arg):
        self.use = None
        self.prefs_scope = None
        self.sqlite_active_db = None
        self.prompt = "CARI$ "

    def do_version(self, arg):
        self.request_callback("version")
        
    def handle_resource(self, action):
        resource = action[0]
        if resource == PreferencesResource.RESOURCE:
            return self.preferences_resource.handle_resource(action)
        elif resource == SqliteResource.RESOURCE:
            return self.sqlite_resource.handle_resource(action)

    def print_colored(self, message, color):
        print("{1}{0}{2}\n".format(message, color, ConsoleColors.ENDC))
