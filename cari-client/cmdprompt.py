from cmd import Cmd
from preferencesresource import PreferencesResource
from sqliteresource import SqliteResource

class CmdPrompt(Cmd):

    prompt = "CARI$ "
    use = None
    prefs_scope = None

    preferences_resource = PreferencesResource()
    sqlite_resource = SqliteResource()

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
            print(request)
            self.request_callback(request)

    # can be used with only "use"
    def do_scopes(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.append("scopes")
            request = self.handle_resource(args)
            self.request_callback(request)

    # can be used with "use" and "scope"
    def do_list(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "list")
            request = self.handle_resource(args)
            self.request_callback(request)

    def do_get(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "get")
            args.append(arg)
            request = self.handle_resource(args)
            self.request_callback(request)

    def do_remove(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "remove")
            args.append(arg)
            request = self.handle_resource(args)
            self.request_callback(request)

    def do_set(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            args = self.create_prefs_args()
            args.insert(1, "set")
            args.extend(arg.split())
            request = self.handle_resource(args)
            self.request_callback(request)

    def create_prefs_args(self):
        args = []
        if self.use is not None:
            args.append(self.use)
        if self.prefs_scope is not None:
            args.append(self.prefs_scope)
        return args
    # /PREFS
    #

    def do_use(self, arg):
        if self.use == PreferencesResource.RESOURCE:
            self.prefs_scope = arg
            self.prompt = "CARI ({0}\{1})$ ".format(self.use, arg)
        else:
            self.use = arg
            self.prompt = "CARI ({0})$ ".format(arg)

    def do_clear(self, arg):
        self.use = None
        self.prefs_scope = None
        self.prompt = "CARI$ "

    def do_version(self, arg):
        request = self.preferences_resource.handle_resource(["prefs", "version"])
        self.request_callback(request)
        
    def handle_resource(self, action):
        resource = action[0]
        if resource == PreferencesResource.RESOURCE:
            return self.preferences_resource.handle_resource(action)
        elif resource == SqliteResource.RESOURCE:
            return self.sqlite_resource.handle_resource(action)
