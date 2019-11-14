package com.kenansoylu.bauproject.misc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class SharedPreferenceManager(val context: AppCompatActivity) {

    fun getData(key: String) : String? {
        val sharedPref = this.context.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString(key, "")
    }

    fun saveData(key: String, value: String) {
        val sharedPref = this.context.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(key, value)
            commit()
        }
    }
}