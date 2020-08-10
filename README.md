# CARI (Console Android Resources Inspector)

[![](https://jitpack.io/v/darekbx/CARI.svg)](https://jitpack.io/#darekbx/CARI)

Android tool used to view and edit resources like Preferences and Sqlite database.

### Requirements
Installed Android Debug Bridge (adb), python3.

### How to use
Connect Android device to the computer, and wait till device is ready, then run: 
> python3 cari-client-standalone.py

**cari-client-standalone.py** file is a compressed version of cari client, this file can be downloaded from release assets.

### Options
  - **-d** provide a device
  - **-p** provide custom port for forward, default is 38300

### Shell commands
  - **use** [resource|prefs_scope|sqlite_database]  - Use resource, scope or sqlite database for further actions
  - **clear**                                       - Clear used resource and scopes
  - **version**                                     - Print CARI Android SDK version

### Resources
  - **prefs**, Android shared preferences wrapper, commands:
    - **scopes** - print all preferences scopes, related to the application context
    - **use** - select preferences scope for further actions
    - **dump** - print all preferences data (can be used with and without scope)
    - **list** - list all keys  (e.g. command: list)
    - **remove** - remove key with value  (e.g. command: remove KeyToDelete)
    - **set** - set value to key (e.g. command: set new_key KeyValue)
    - **get** - get key value (e.g. command: get my_key)
  - **sqlite**, Android SQLite wrapper, commands:
    - **databases** - print all databases, related to the application context
    - **use** - select sqlite database for further actions
    - **tables** - print all tables from selected database
    - **[query]** - execute SQLite query, when database is used (e.g. command: SELECT * FROM table or with prefix: q SELECT 1)

### How to list keys from preferences scope:
  1. Run CARI: 
  > python3 cari-client-standalone.py
  2. In CARI shell type: 
  > use prefs
  3. Dump all scopes, by typing in shell: 
  > dump
  4. Use preferences scope (eg app_preferences), by typing in shell: 
  > use app_preferences   
  5. Type in shell to list all keys: 
  > list
  
### How to execute an SQLite query:
  1. Run CARI: 
  > python3 cari-client-standalone.py
  2. In CARI shell type: 
  > use sqlite
  3. Print all databases, by typing in shell: 
  > databases
  4. Select database, by typing in shell: 
  > use room_db   
  5. Type in shell to execute an query: 
  > SELECT * FROM table ORDER BY _id DESC

### Android integration
  1. Add to project **build.gradle** file those lines:
  ```groovy
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
  }
  ```
  2. Add to project **build.gardle** CARI sdk dependency:
  ```groovy
  implementation 'com.github.darekbx:CARI:{latest release}'
  ```
  3. Initialize CARI in your Application class:
  ```kotlin
  class MyApplication : Application() {
      override fun onCreate() {
          super.onCreate()
          CARI.initialize(applicationContext)
      }
  }
  ```
  4. Add **INTERNET** permission to **AndroidManifest.xml** file:
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  ```

<br />

### Any suggestions? Just add an issue.

<br />

### License
```
Copyright 2019 DarekBx

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
