package app.remux.shared_storage.remux_shared_storage_plugin

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** RemuxSharedStoragePlugin */
class RemuxSharedStoragePlugin : FlutterPlugin, MethodCallHandler,
    PluginRegistry.ActivityResultListener, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var result: MethodChannel.Result? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "remux_shared_storage_plugin")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "openDirectoryPicker") {
            this.result = result
            openDirectoryPicker()
        } else if (call.method == "openFilePicker") {
            this.result = result
            openFilePicker()
        } else if (call.method == "createFile") {
            this.result = result

            val dirUri = call.argument<String>("dirUri")
            val fileName = call.argument<String>("fileName")
            val mimeType = call.argument<String>("mimeType")

            if (dirUri != null && fileName != null && mimeType != null) {
                createFile(dirUri, fileName, mimeType)
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "getFileSizeFromUri") {
            this.result = result
            val fileUri = call.argument<String>("fileUri")

            if (fileUri != null) {
                getFileSizeFromUri(fileUri)
            } else {
                result.error("Invalid arguments", null, null);
            }
        } else if (call.method == "getUniqueFileName") {
            this.result = result
            val dirUri = call.argument<String>("directoryUri")
            val fileName = call.argument<String>("fileName")
            val fileExtension = call.argument<String>("fileExtension")

            if (dirUri != null && fileName != null && fileExtension != null) {
                getUniqueFileName(Uri.parse(dirUri), fileName, fileExtension)
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "getFileName")  {
            this.result = result

            val fileUri = call.argument<String>("fileUri")

            if (fileUri != null) {
                getFileName(activity!!.contentResolver, Uri.parse(fileUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "getDirectoryName") {
            this.result = result

            val directoryUri = call.argument<String>("directoryUri")

            if (directoryUri != null) {
                getDirectoryName(activity!!, Uri.parse(directoryUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "shareFile") {
            this.result = result
            val fileUri = call.argument<String>("fileUri")

            if (fileUri != null) {
                shareFile(activity!!, Uri.parse(fileUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "openFileWithExternalApp") {
            this.result = result
            val fileUri = call.argument<String>("fileUri")

            if (fileUri != null) {
                openFileWithExternalApp(activity!!, Uri.parse(fileUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "moveFileToDirectory") {
            this.result = result
            val fileUri = call.argument<String>("fileUri")
            val directoryUri = call.argument<String>("directoryUri")

            if (fileUri != null && directoryUri != null) {
                moveFileToDirectory(Uri.parse(fileUri), Uri.parse(directoryUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else {
            result.notImplemented()
        }
    }

    private fun openDirectoryPicker() {
        val intent = SharedStorageUtils.getDirectoryPickerIntent()

        if (intent != null && activity != null) {
            activity?.startActivityForResult(
                intent,
                SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE
            )
        } else if (activity == null) {
            result?.error("Activity is null", null, null)
        } else {
            result?.error("Requires API level 21 or higher", null, null)
        }
    }

    private fun openFilePicker() {
        val intent = SharedStorageUtils.getFilePickerIntent(FilePickerMode.MULTIPLE)
        activity?.startActivityForResult(intent, SharedStorageUtils.FILE_PICKER_REQUEST_CODE)
    }

    private fun getFileSizeFromUri(uriString: String) {
        var fileSize: Long? = null
        val uri = Uri.parse(uriString)
        activity?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) {
                fileSize = cursor.getLong(sizeIndex)
            }
        }

        result?.success(fileSize)
    }

    private fun createFile(dirUri: String, fileName: String, mimeType: String) {
        if (activity != null) {
            val pickedDir = DocumentFile.fromTreeUri(activity!!, Uri.parse(dirUri))
            val newFile = pickedDir?.createFile(mimeType, fileName)
            val newFileUri = newFile?.uri.toString()
            result?.success(newFileUri)
        } else {
            result?.error("Activity is null", null, null)
        }
    }

    private fun getUniqueFileName(directoryUri: Uri, fileName: String, fileExtension: String) {
        val context = activity
        val contentResolver = context?.contentResolver
        if (contentResolver == null) {
            result?.error("Activity is null", null, null)
            return
        }

        val directory = DocumentFile.fromTreeUri(context, directoryUri)
        var uniqueFileName = "$fileName.$fileExtension"

        var counter = 0
        while (fileExists(directory, uniqueFileName)) {
            counter++
            uniqueFileName = "${fileName}_$counter.$fileExtension"
        }

        result?.success(uniqueFileName)
    }

    private fun fileExists(directory: DocumentFile?, fileName: String): Boolean {
        directory?.listFiles()?.forEach { file ->
            if (file.name == fileName) {
                return true
            }
        }
        return false
    }

    private fun getFileName(contentResolver: ContentResolver, fileUri: Uri) {
        val cursor = contentResolver.query(fileUri, null, null, null, null)
        var fileName: String? = null

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                fileName = it.getString(nameIndex)
            }
        }

        result?.success(fileName)
    }

    private fun getDirectoryName(context: Context, uri: Uri?) {
        val directoryUri = uri ?: getPersistentDirectoryUri()

        if (directoryUri == null) {
            result?.error("No directory URI available", null, null)
            return
        }

        val documentFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && DocumentsContract.isTreeUri(directoryUri)) {
            DocumentFile.fromTreeUri(context, directoryUri)
        } else {
            DocumentFile.fromSingleUri(context, directoryUri)
        }

        val directoryName = documentFile?.let { docFile ->
            if (docFile.isFile) {
                // If it's a file, get the parent directory's name
                docFile.parentFile?.name
            } else {
                // If it's a directory, get the directory's name
                docFile.name
            }
        }

        result?.success(directoryName)
    }

    private fun getPersistentDirectoryUri(): Uri? {
        val sharedPrefs = activity?.getSharedPreferences("RemuxSharedStoragePrefs", Context.MODE_PRIVATE)
        val uriString = sharedPrefs?.getString("directoryUri", null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    private fun persistDirectoryUri(uri: Uri) {
        val sharedPrefs = activity?.getSharedPreferences("RemuxSharedStoragePrefs", Context.MODE_PRIVATE)
        sharedPrefs?.edit()?.putString("directoryUri", uri.toString())?.apply()
    }


    private fun shareFile(context: Context, fileUri: Uri) {
        // Determine the MIME type of the file
        val mimeType = context.contentResolver.getType(fileUri)

        // Create the share intent with the determined MIME type
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Start the chooser activity to let the user select an app to share the file with
        val chooser = Intent.createChooser(shareIntent, "Share File")
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
            result?.success(true)
        } else {
            Toast.makeText(context, "No app can handle this file", Toast.LENGTH_SHORT).show()
            result?.success(false)
        }
    }

    private fun openFileWithExternalApp(context: Context, fileUri: Uri) {
        // Determine the MIME type of the file
        val mimeType = context.contentResolver.getType(fileUri)

        // Create an intent with action view
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary read permission
        }

        // Check if there's an app that can handle this intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            result?.success(true)
        } else {
            // Handle the situation where no app can handle the intent
            // You might want to show a message to the user
            Toast.makeText(context, "No app can handle this request", Toast.LENGTH_SHORT).show()
            result?.success(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        // Directory picker
        if (requestCode == SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data?.data != null) {
                val uri = data.data!!

                val contentResolver = activity?.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver?.takePersistableUriPermission(uri, takeFlags)

                persistDirectoryUri(uri)

                result?.success(uri.toString())
                return true
            } else if (resultCode == Activity.RESULT_CANCELED) {
                result?.success(null)
                return true
            }
        }

        // File picker
        if (requestCode == SharedStorageUtils.FILE_PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                when {
                    data?.clipData != null -> {
                        // Multiple files selected
                        val count = data.clipData!!.itemCount
                        val uris = ArrayList<String>()
                        for (i in 0 until count) {
                            val uri = data.clipData!!.getItemAt(i).uri
                            uris.add(uri.toString())
                        }
                        result?.success(uris)
                    }
                    data?.data != null -> {
                        // Single file selected
                        val uri = data.data!!
                        result?.success(listOf(uri.toString()))
                    }
                    else -> result?.success(listOf<String>())
                }
                return true
            } else if (resultCode == Activity.RESULT_CANCELED) {
                result?.success(listOf<String>())
                return true
            }
        }

        return false
    }

    private fun moveFileToDirectory(fileUri: Uri, directoryUri: Uri) {
        val bufferSize = 1024 * 1024 // 1 MB buffer size

        try {
            val context = activity ?: return
            val contentResolver = context.contentResolver

            // Retrieve the file name from the file URI
            val fileName = getFileNameFromUri(contentResolver, fileUri)
            if (fileName == null) {
                result?.error("Error getting file name", null, null)
                return
            }

            // Open an InputStream from the file URI
            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                // Create a new file in the selected directory
                val directory = DocumentFile.fromTreeUri(context, directoryUri)
                val newFile = directory?.createFile("file/*", fileName)

                // Open an OutputStream to the new file and write in chunks
                newFile?.uri?.let { newFileUri ->
                    contentResolver.openOutputStream(newFileUri)?.use { outputStream ->
                        val buffer = ByteArray(bufferSize)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }

                        // File copied, now delete the original file
                        val originalFile = DocumentFile.fromSingleUri(context, fileUri)
                        if (originalFile?.delete() == true) {
                            result?.success(newFileUri.toString())
                        } else {
                            result?.error("Error deleting original file", null, null)
                        }
                    } ?: run {
                        result?.error("Error opening output stream", null, null)
                    }
                } ?: run {
                    result?.error("Error creating new file in the directory", null, null)
                }
            } ?: run {
                result?.error("Error opening input stream", null, null)
            }
        } catch (e: Exception) {
            result?.error("Error moving file", e.message, null)
        }
    }

    private fun getFileNameFromUri(contentResolver: ContentResolver, fileUri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    companion object {
        private const val TAG = "RemuxSharedStoragePlugi"
    }

}
