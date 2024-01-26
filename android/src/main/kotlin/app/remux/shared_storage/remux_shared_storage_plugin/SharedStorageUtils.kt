package app.remux.shared_storage.remux_shared_storage_plugin

import android.content.Intent
import android.os.Build
import android.util.Log

enum class FilePickerMode {SINGLE, MULTIPLE}

class SharedStorageUtils {
    companion object {
        private const val TAG = "SharedStorageUtils"
        const val DIRECTORY_PICKER_REQUEST_CODE = 1523
        const val FILE_PICKER_REQUEST_CODE = 1524
        fun getDirectoryPickerIntent() : Intent? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent
            } else {
                Log.w(TAG, "Requires API level 21 or higher")
                null
            }
        }

        fun getFilePickerIntent(mode: FilePickerMode): Intent {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            if (mode == FilePickerMode.MULTIPLE) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }

            // Set flags for persistable URI permission
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

            return intent
        }
    }
}