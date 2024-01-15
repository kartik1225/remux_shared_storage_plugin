
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
}
