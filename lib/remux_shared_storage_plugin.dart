
import 'remux_shared_storage_plugin_platform_interface.dart';

class RemuxSharedStoragePlugin {
  Future<String?> openDirectoryPicker() {
    return RemuxSharedStoragePluginPlatform.instance.openDirectoryPicker();
  }

  Future<List<String>> openFilePicker() {
    return RemuxSharedStoragePluginPlatform.instance.openFilePicker();
  }

  Future<String?> createFile(String dirUri, String fileName, String mimeType) {
    return RemuxSharedStoragePluginPlatform.instance.createFile(dirUri, fileName, mimeType);
  }

  Future<int?> getFileSizeFromUri(String fileUri) {
    return RemuxSharedStoragePluginPlatform.instance.getFileSizeFromUri(fileUri);
  }

  Future<String?> getUniqueFileName(String directoryUri, String fileName, String fileExtension) {
    return RemuxSharedStoragePluginPlatform.instance.getUniqueFileName(directoryUri, fileName, fileExtension);
  }

  Future<String?> getFileName(String fileUri) {
    return RemuxSharedStoragePluginPlatform.instance.getFileName(fileUri);
  }

  Future<String?> getDirectoryName(String directoryUri) {
    return RemuxSharedStoragePluginPlatform.instance.getDirectoryName(directoryUri);
  }

  Future<String?> getFileNameWithoutExtension(String fileUri) async {
    final result = await getFileName(fileUri);
    return result?.split('.').first;
  }

  Future<bool> shareFile(String fileUri) {
    return RemuxSharedStoragePluginPlatform.instance.shareFile(fileUri);
  }

  Future<bool> openFileWithExternalApp(String fileUri) {
    return RemuxSharedStoragePluginPlatform.instance.openFileWithExternalApp(fileUri);
  }

  Future<String?> moveFileToDirectory(String fileUri, String directoryUri) {
    return RemuxSharedStoragePluginPlatform.instance.moveFileToDirectory(fileUri, directoryUri);
  }
}
