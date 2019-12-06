# CARI (Console Android Resources Inspector)

Android tool used to view and edit resources like Preferences and Sqlite database.

#### Requirements
Installed Android Debug Bridge (adb), python3.

#### How to use
Connect Android device to the computer, and wait till device is ready, then run: 
> python3 cariclient.py

#### Options
  - **-d** provide a device
  - **-p** provide custom port for forward, default is 38300

#### Shell commands
  - **use** {resource|prefs_scope}  - Use resource or scope for further actions
  - **clear**                       - Clear used resource and scopes
  - **version**                     - Print CARI Android SDK version

#### Resources
  - **prefs**, Android shared preferences wrapper.
    
    Commands:
    - **dump** - print all preferences data (can be used with scope)
    - **list** - list all keys  (e.g. command: list)
    - **remove** - remove key with value  (e.g. command: remove KeyToDelete)
    - **set** - set value to key (e.g. command: set new_key KeyValue)
    - **get** - get key value (e.g. command: get my_key)
  - **sqlite**, *TODO*


#### How to list keys from preferences scope:
  1. Run CARI: 
  > python3 cariclient.py
  2. In CARI Shell type: 
  > use prefs
  3. Dump all scopes, by typing in shell: 
  > dump
  4. Use preferences scope (eg app_preferences), by typing in shell: 
  > use app_preferences   
  5. Type in shell to list all keys: 
  > list
