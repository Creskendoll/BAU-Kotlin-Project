package com.kenansoylu.bauproject.misc

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView


class DisplayImage(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        bmImage.tag = urldisplay
        var mIcon11: Bitmap? = null
        try {
            val inStream = java.net.URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(inStream)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null)
            bmImage.setImageBitmap(result)
    }
}