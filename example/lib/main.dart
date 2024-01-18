import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:remux_shared_storage_plugin/remux_shared_storage_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _remuxSharedStoragePlugin = RemuxSharedStoragePlugin();

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> openDirectoryPicker() async {
    try {
      final pickedFiles = await _remuxSharedStoragePlugin.openDirectoryPicker();
      print("pickedFiles $pickedFiles");
    } on PlatformException {
      print('Failed to get picked files.');
    }
  }

  Future<void> openFilePicker() async {
    try {
      final pickedFiles = await _remuxSharedStoragePlugin.openFilePicker();
      print(pickedFiles);
    } on PlatformException {
      print('Failed to get picked files.');
    }
  }

  Future<void> handleCreateFile() async {
    try {
      final dirUri = await _remuxSharedStoragePlugin.openDirectoryPicker();

      if (dirUri != null) {
        final fileName =
            "${DateTime.now().millisecondsSinceEpoch.toString()}.avi";
        final createdFile = await _remuxSharedStoragePlugin.createFile(
            dirUri, fileName, "video/avi");
        print(createdFile);
      } else {
        print('No directory picked');
      }
    } on PlatformException {
      print('Failed to get picked files.');
    }
  }

  Future<void> getFileSize() async {
    final pickedFile = await _remuxSharedStoragePlugin.openFilePicker();

    if (pickedFile.isNotEmpty) {
      final fileSize =
          await _remuxSharedStoragePlugin.getFileSizeFromUri(pickedFile.first);
      print("fileSize $fileSize");
    } else {
      print('No file picked');
    }
  }

  Future<void> getUniqueFileName() async {
    final pickedFile = await _remuxSharedStoragePlugin.openDirectoryPicker();

    if (pickedFile != null) {
      final fileSize = await _remuxSharedStoragePlugin.getUniqueFileName(
        pickedFile,
        "fileNameThing",
        "mkv",
      );
      print("getUniqueFileName: $fileSize");
    } else {
      print('No file picked');
    }
  }

  Future<void> getFileName() async {
    final pickedFile = await _remuxSharedStoragePlugin.openFilePicker();

    if (pickedFile.isNotEmpty) {
      final fileName = await _remuxSharedStoragePlugin.getFileName(pickedFile.first);
      print("getFileName: $fileName");
    } else {
      print('No file picked');
    }
  }


  Future<void> getDirectoryName() async {
    final pickedFile = await _remuxSharedStoragePlugin.openDirectoryPicker();

    if (pickedFile != null) {
      final directoryName = await _remuxSharedStoragePlugin.getDirectoryName(pickedFile);
      print("getDirectoryName: $directoryName");
    } else {
      print('No file picked');
    }
  }

  Future<void> openInExternalApp() async {
    final pickedFile = await _remuxSharedStoragePlugin.openFilePicker();

    if (pickedFile.isNotEmpty) {
      final result = await _remuxSharedStoragePlugin.openInExternalApp(pickedFile.first);
      print("openInExternalApp: $result");
    } else {
      print('No file picked');
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                    onPressed: () {
                      openDirectoryPicker();
                    },
                    child: const Text("Directory picker"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      openFilePicker();
                    },
                    child: const Text("File picker"),
                  ),

                  // open external app
                  ElevatedButton(
                    onPressed: () {
                      openInExternalApp();
                    },
                    child: const Text("Share"),
                  ),
                ],
              ),
              ElevatedButton(
                onPressed: () {
                  handleCreateFile();
                },
                child: const Text("Pick dir and create file"),
              ),
              Row(
                children: [
                  ElevatedButton(
                    onPressed: () {
                      getFileSize();
                    },
                    child: const Text("Get file size"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      getUniqueFileName();
                    },
                    child: const Text("Get unique file name"),
                  ),
                ],
              ),
              Row(
                children: [
                  ElevatedButton(
                    onPressed: () {
                      getFileName();
                    },
                    child: const Text("Get file name"),
                  ),
                  ElevatedButton(
                    onPressed: () {
                      getDirectoryName();
                    },
                    child: const Text("Get directory name"),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
