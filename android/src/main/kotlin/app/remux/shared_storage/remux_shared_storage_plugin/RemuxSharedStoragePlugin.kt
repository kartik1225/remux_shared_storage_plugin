package app.remux.shared_storage.remux_shared_storage_plugin

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
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
import java.io.File
import java.io.IOException


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
        } else if (call.method == "getFileName") {
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
        } else if (call.method == "copyFileToGallery") {
            this.result = result
            val fileUri = call.argument<String>("fileUri")

            if (fileUri != null) {
                copyFileToGallery(Uri.parse(fileUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "hasPersistableUriPermission") {
            this.result = result
            val uriString = call.argument<String>("uriString")

            if (uriString != null) {
                result.success(hasPersistableUriPermission(uriString))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "tryTakePersistableUriPermission") {
            this.result = result
            val uriString = call.argument<String>("uriString")

            if (uriString != null) {
                result.success(tryTakePersistableUriPermission(Uri.parse(uriString)))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "fileUriExists") {
            this.result = result
            val uriString = call.argument<String>("uriString")

            if (uriString != null) {
                result.success(fileUriExists(Uri.parse(uriString), activity!!))
            } else {
                result.error("Invalid arguments", null, null)
            }

        } else if (call.method == "deleteFileFromUri") {
            this.result = result
            val fileUri = call.argument<String>("fileUri")

            if (fileUri != null) {
                result.success(deleteFileFromUri(fileUri))
            } else {
                result.error("Invalid arguments", null, null)
            }
        } else if (call.method == "copyFileToCache") {
            val contentUri = call.argument<String>("contentUri")
            if (contentUri != null) {
                copyFileToCache(Uri.parse(contentUri), result)
            } else {
                result.error("Invalid arguments", "Content URI is required", null)
            }
        } else {
            result.notImplemented()
        }
    }

    private fun openDirectoryPicker() {
        val intent = SharedStorageUtils.getDirectoryPickerIntent()

        if (intent != null && activity != null) {
            activity?.startActivityForResult(
                intent, SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE
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
        val activityContext = activity
        if (activityContext != null) {
            val pickedDir = DocumentFile.fromTreeUri(activityContext, Uri.parse(dirUri))
            val newFile = pickedDir?.createFile(mimeType, fileName)
            val newFileUri = newFile?.uri

            // Check if new file URI is not null and take persistable URI permission if needed
            if (newFileUri != null) {
                val takeFlags: Int =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                try {
                    // Attempt to take persistable permissions
                    activityContext.contentResolver.takePersistableUriPermission(
                        newFileUri, takeFlags
                    )
                } catch (e: SecurityException) {
                    // Handle the exception if persistable permissions can't be taken
                    Log.e(TAG, "Error taking persistable URI permission: ${e.message}")
                }
                result?.success(newFileUri.toString())
            } else {
                result?.error("File creation failed", null, null)
            }
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

        if (uri == null) {
            result?.error("No directory URI available", null, null)
            return
        }

        val documentFile =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && DocumentsContract.isTreeUri(
                    uri
                )
            ) {
                DocumentFile.fromTreeUri(context, uri)
            } else {
                DocumentFile.fromSingleUri(context, uri)
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

    private fun hasPersistableUriPermission(uriString: String): Boolean {
        val uri = Uri.parse(uriString)
        val uriPermissions = activity?.contentResolver?.persistedUriPermissions

        return uriPermissions?.any { permission ->
            permission.uri == uri
        } ?: false
    }

    private fun tryTakePersistableUriPermission(uri: Uri): Boolean {
        val contentResolver = activity?.contentResolver
        val takeFlags: Int =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return try {
            // Attempt to take persistable URI permission
            contentResolver?.takePersistableUriPermission(uri, takeFlags)
            true // Successfully took permission
        } catch (e: SecurityException) {
            // Failed to take permission
            false
        }
    }

    private fun fileUriExists(uri: Uri, context: Context): Boolean {
        try {
            val documentFile = DocumentFile.fromSingleUri(context, uri)

            return documentFile?.exists() ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if file URI exists: ${e.message}")
            return false
        }
    }

    private fun deleteFileFromUri(fileUriString: String): Boolean {
        val fileUri = Uri.parse(fileUriString)
        val context = activity ?: return false

        return try {
            val documentFile = DocumentFile.fromSingleUri(context, fileUri)
            documentFile?.delete() ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting file: ${e.message}")
            false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val contentResolver = activity?.contentResolver

        // Directory picker
        if (requestCode == SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data?.data != null) {
                val uri = data.data!!
                val takeFlags: Int =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver?.takePersistableUriPermission(uri, takeFlags)

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
                val uris = ArrayList<String>()
                val takeFlags: Int =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                // Handling multiple file selection
                data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        contentResolver?.takePersistableUriPermission(uri, takeFlags)
                        uris.add(uri.toString())
                    }
                }

                // Handling single file selection
                data?.data?.let { uri ->
                    contentResolver?.takePersistableUriPermission(uri, takeFlags)
                    uris.add(uri.toString())
                }

                result?.success(uris)
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

    private fun copyFileToGallery(fileUri: Uri): String {
        val context = activity ?: return "Application context is null"

        try {
            val mimeType = context.contentResolver.getType(fileUri)
            val isVideo = mimeType?.startsWith("video") == true
            val fileName = System.currentTimeMillis().toString()
            val mediaContentUri =
                if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
                    )
                }
            }

            val outputUri = context.contentResolver.insert(mediaContentUri, values)
                ?: return "Failed to create new MediaStore entry."

            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    val buffer = ByteArray(10240) // 10KB buffer
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                } ?: return "Failed to open output stream."
            } ?: return "Failed to open input stream."

            // Trigger media scanner only for versions below Android 10
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                MediaScannerConnection.scanFile(context, arrayOf(outputUri.toString()), null, null)
            }

            return outputUri.toString()
        } catch (e: IOException) {
            return "IOException occurred: ${e.message}"
        }
    }

    private fun copyFileToCache(contentUri: Uri, result: Result) {
        val context = activity ?: run {
            result.error("No activity", "Activity is null", null)
            return
        }

        try {
            val contentResolver = context.contentResolver
            val fileName = getFileNameFromUri(contentResolver, contentUri) ?: run {
                result.error("File name error", "Unable to get file name", null)
                return
            }

            val cacheDir = context.cacheDir
            val destinationFile = File(cacheDir, fileName)

            contentResolver.openInputStream(contentUri)?.use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: run {
                result.error("Input stream error", "Unable to open input stream", null)
                return
            }

            // Ensure the file is readable
            if (!destinationFile.setReadable(true, false)) {
                Log.w(TAG, "Failed to set file as readable")
            }

            result.success(destinationFile.absolutePath)
        } catch (e: IOException) {
            result.error("IO Exception", "Error copying file to cache: ${e.message}", null)
        } catch (e: SecurityException) {
            result.error("Security Exception", "Permission denied: ${e.message}", null)
        } catch (e: Exception) {
            result.error("Unknown Exception", "Error: ${e.message}", null)
        }
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
