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

  @override
  Future<String?> getDirectoryName(String directoryUri) async {
    final result = await methodChannel.invokeMethod<String>('getDirectoryName', <String, dynamic>{
      'directoryUri': directoryUri,
    });
    return result;
  }

  @override
  Future<bool> shareFile(String fileUri) async {
    final result = await methodChannel.invokeMethod<bool>('shareFile', <String, dynamic>{
      'fileUri': fileUri,
    });
    return result ?? false;
  }

  @override
  Future<bool> openFileWithExternalApp(String fileUri) async {
    final result = await methodChannel.invokeMethod<bool>('openFileWithExternalApp', <String, dynamic>{
      'fileUri': fileUri,
    });
    return result ?? false;
  }

  @override
  Future<String?> moveFileToDirectory(String fileUri, String directoryUri) async {
    final result = await methodChannel.invokeMethod<String>('moveFileToDirectory', <String, dynamic>{
      'fileUri': fileUri,
      'directoryUri': directoryUri,
    });
    return result;
  }

  @override
  Future<String?> copyFileToGallery(String fileUri) async {
    final result = await methodChannel.invokeMethod<String>('copyFileToGallery', <String, dynamic>{
      'fileUri': fileUri,
    });
    return result;
  }

  @override
  Future<bool> hasPersistableUriPermission(String uriString) async {
    final result = await methodChannel.invokeMethod<bool>('hasPersistableUriPermission', <String, dynamic>{
      'uriString': uriString,
    });
    return result ?? false;
  }

  @override
  Future<bool> tryTakePersistableUriPermission(String uriString) async {
    final result = await methodChannel.invokeMethod<bool>('tryTakePersistableUriPermission', <String, dynamic>{
      'uriString': uriString,
    });
    return result ?? false;
  }

  @override
  Future<bool> fileUriExists(String uriString) async {
    final result = await methodChannel.invokeMethod<bool>('fileUriExists', <String, dynamic>{
      'uriString': uriString,
    });
    return result ?? false;
  }

  @override
  Future<bool> deleteFileFromUri(String fileUri) async {
    final result = await methodChannel.invokeMethod<bool>('deleteFileFromUri', <String, dynamic>{
      'fileUri': fileUri,
    });
    return result ?? false;
  }
}
