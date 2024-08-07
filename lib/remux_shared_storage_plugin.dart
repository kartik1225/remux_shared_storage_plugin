
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

  Future<String?> copyFileToGallery(String fileUri) async {
    return RemuxSharedStoragePluginPlatform.instance.copyFileToGallery(fileUri);
  }

  Future<bool> hasPersistableUriPermission(String uriString) {
    return RemuxSharedStoragePluginPlatform.instance.hasPersistableUriPermission(uriString);
  }

  Future<bool> tryTakePersistableUriPermission(String uriString) {
    return RemuxSharedStoragePluginPlatform.instance.tryTakePersistableUriPermission(uriString);
  }

  Future<bool> fileUriExists(String uriString) {
    return RemuxSharedStoragePluginPlatform.instance.fileUriExists(uriString);
  }

  Future<bool> deleteFileFromUri(String fileUri) {
    return RemuxSharedStoragePluginPlatform.instance.deleteFileFromUri(fileUri);
  }

  Future<String?> copyFileToCache(String contentUri) {
    return RemuxSharedStoragePluginPlatform.instance.copyFileToCache(contentUri);
  }

}
