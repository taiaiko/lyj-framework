Installer
---------
Files to install goes here.

You can deploy zip archives containing *install.json* configuration file.

Sample install.json file with absolute target path:
```json
{
    "uid":"aap-unique-id",
    "target":"/target/directory/to/copy/files/",
    "action-before":"stop",
    "action-after":"start"
}
```
Sample install.json file with relative target path:
```json
{
    "uid":"aap-unique-id",
    "target":"./sub_directory/to/copy/files/",
    "action-before":"stop",
    "action-after":"start"
}
```

