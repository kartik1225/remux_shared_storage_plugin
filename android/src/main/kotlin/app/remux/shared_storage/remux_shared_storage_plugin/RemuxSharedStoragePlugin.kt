package app.remux.shared_storage.remux_shared_storage_plugin

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
        } else {
            result.notImplemented()
        }
    }

    private fun openDirectoryPicker() {
        val intent = SharedStorageUtils.getDirectoryPickerIntent()

        if (intent != null && activity != null) {
            activity?.startActivityForResult(intent, SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        // directory picker
        if (requestCode == SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val uri = data.data!!
                result?.success(uri.toString())
            } else {
                result?.success(null)
            }
            return true
        } else if (requestCode == SharedStorageUtils.DIRECTORY_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            result?.success(null)
            return true
        }

        // file picker
        if (requestCode == SharedStorageUtils.FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                // Multiple files selected
                val count = data.clipData!!.itemCount
                val uris = ArrayList<String>()
                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    uris.add(uri.toString())
                }

                result?.success(uris)
            } else if (data?.data != null) {
                // Single file selected
                val uri = data.data!!

                result?.success(listOf(uri.toString()))
            } else {
                result?.success(listOf<String>())
            }
            return true
        } else if (requestCode == SharedStorageUtils.FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            result?.success(listOf<String>())
            return true
        }

        return false
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
