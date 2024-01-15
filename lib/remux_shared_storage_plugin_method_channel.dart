import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'remux_shared_storage_plugin_platform_interface.dart';

/// An implementation of [RemuxSharedStoragePluginPlatform] that uses method channels.
class MethodChannelRemuxSharedStoragePlugin extends RemuxSharedStoragePluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('remux_shared_storage_plugin');

  @override
  Future<String?> openDirectoryPicker() async {
    final result = await methodChannel.invokeMethod<String>('openDirectoryPicker');
    return result;
  }

  @override
  Future<List<String>> openFilePicker() async {
    final result = await methodChannel.invokeMethod<List<dynamic>>('openFilePicker');
    return result?.map((item) => item as String).toList() ?? [];
  }

  @override
  Future<String?> createFile(String dirUri, String fileName, String mimeType) async {
    final result = await methodChannel.invokeMethod<String>('createFile', <String, dynamic>{
      'dirUri': dirUri,
      'fileName': fileName,
      'mimeType': mimeType,
    });
    return result;
  }

  @override
  Future<int?> getFileSizeFromUri(String fileUri) async {
    final result = await methodChannel.invokeMethod<int>('getFileSizeFromUri', <String, dynamic>{
      'fileUri': fileUri,
    });
    return result;
  }

  @override
  Future<String?> getUniqueFileName(String directoryUri, String fileName, String fileExtension) async {
    final result = await methodChannel.invokeMethod<String>('getUniqueFileName', <String, dynamic>{
      'directoryUri': directoryUri,
      'fileName': fileName,
      'fileExtension': fileExtension,
    });
    return result;
  }

  @override
  Future<String?> getFileName(String fileUri) async {
    final result = await methodChannel.invokeMethod<String>('getFileName', <String, dynamic>{
      'fileUri': fileUri,
    });
    return result;
  }
}
