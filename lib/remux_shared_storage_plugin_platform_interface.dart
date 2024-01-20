import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'remux_shared_storage_plugin_method_channel.dart';

abstract class RemuxSharedStoragePluginPlatform extends PlatformInterface {
  /// Constructs a RemuxSharedStoragePluginPlatform.
  RemuxSharedStoragePluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static RemuxSharedStoragePluginPlatform _instance = MethodChannelRemuxSharedStoragePlugin();

  /// The default instance of [RemuxSharedStoragePluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelRemuxSharedStoragePlugin].
  static RemuxSharedStoragePluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [RemuxSharedStoragePluginPlatform] when
  /// they register themselves.
  static set instance(RemuxSharedStoragePluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> openDirectoryPicker() {
    throw UnimplementedError('openDirectoryPicker() has not been implemented.');
  }

  Future<List<String>> openFilePicker() {
    throw UnimplementedError('openFilePicker() has not been implemented.');
  }

  Future<String?> createFile(String dirUri, String fileName, String mimeType) {
    throw UnimplementedError('createFile() has not been implemented.');
  }

  Future<int?> getFileSizeFromUri(String fileUri) {
    throw UnimplementedError('getFreeSpace() has not been implemented.');
  }

  Future<String?> getUniqueFileName(String directoryUri, String fileName, String fileExtension) {
    throw UnimplementedError('getUniqueFileName() has not been implemented.');
  }

  Future<String?> getFileName(String fileUri) {
    throw UnimplementedError('getFileName() has not been implemented.');
  }

  Future<String?> getDirectoryName(String directoryUri) {
    throw UnimplementedError('getDirectoryName() has not been implemented.');
  }

  Future<bool> shareFile(String fileUri) {
    throw UnimplementedError('shareFile() has not been implemented.');
  }

  Future<bool> openFileWithExternalApp(String fileUri) {
    throw UnimplementedError('openFileWithExternalApp() has not been implemented.');
  }

  Future<String?> moveFileToDirectory(String fileUri, String directoryUri) {
    throw UnimplementedError('moveFileToDirectory() has not been implemented.');
  }

  Future<String?> copyFileToGallery(String fileUri) {
    throw UnimplementedError('copyFileToGallery() has not been implemented.');
  }
}
