package com.kenansoylu.bauproject.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import com.kenansoylu.bauproject.R
import com.kenansoylu.bauproject.services.ResourceService
import java.io.ByteArrayOutputStream


class ChangeAvatarActivity : AppCompatActivity() {

    private lateinit var resourceService: ResourceService
    private lateinit var loadingDialog: ProgressDialog

    private val GALLERY_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)

        resourceService = ResourceService()

        findViewById<Button>(R.id.pickImageBtn).setOnClickListener {
            //Create an Intent with action as ACTION_PICK
            val intent = Intent(Intent.ACTION_PICK)
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.type = "image/*"
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        // Set on click to default avatars
        val root = findViewById<TableLayout>(R.id.change_avatar_root)
        getViewsByTag(root, "default_avatar").forEach {
            val imgView = it as ImageView
            it.setOnClickListener {
                //  Convert bitmap to byte array
                val bitmapDrawable = imgView.drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                // File location
                val savePath = "avatars/${byteArray.hashCode()}"

                loadingDialog = ProgressDialog.show(
                    this@ChangeAvatarActivity, "",
                    "Uploading image...", true
                )

                resourceService.writeData(savePath, byteArray, ::onPickAvatar, ::onError)
            }
        }
    }

    // https://stackoverflow.com/questions/5062264/find-all-views-with-tag
    private fun getViewsByTag(root: ViewGroup, tag: String?): ArrayList<View> {
        val views = ArrayList<View>()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            val child = root.getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(getViewsByTag(child, tag))
            }
            val tagObj = child.tag
            if (tagObj != null && tagObj == tag) {
                views.add(child)
            }
        }
        return views
    }

    private fun onPickAvatar(url: String) {
//        Log.d("AVATAR_URL", url)

        loadingDialog.dismiss()
        val profileIntent = Intent(this@ChangeAvatarActivity, ProfileActivity::class.java)
        profileIntent.putExtra("avatar_url", url)
        setResult(RESULT_OK, profileIntent)
        finish()
    }

    private fun onError(e: Exception?) {
        e?.let {
            Log.e("AVATAR", e.message)
        }
    }

    // https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.getScheme()!!.equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    if (data != null) {
                        //data.getData returns the content URI for the selected Image
                        val selectedImage = data.data!!
                        val inputStream = this@ChangeAvatarActivity.contentResolver.openInputStream(selectedImage)
                        val fileName = getFileName(selectedImage)
                        val savePath = "avatars/${fileName}"
                        val byteArray = inputStream?.readBytes()!!

                        loadingDialog = ProgressDialog.show(
                            this@ChangeAvatarActivity, "",
                            "Uploading image...", true
                        )
                        resourceService.writeData(savePath, byteArray, ::onPickAvatar, ::onError)
                    }
                }
            }
    }

}
