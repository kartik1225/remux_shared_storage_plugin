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
            "${DateTime
            .now()
            .millisecondsSinceEpoch
            .toString()}.avi";
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
                ],
              ),
              ElevatedButton(
                onPressed: () {
                  handleCreateFile();
                },
                child: const Text("Pick dir and create file"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}