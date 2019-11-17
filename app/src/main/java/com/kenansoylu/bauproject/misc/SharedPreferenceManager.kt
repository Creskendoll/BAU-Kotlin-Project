package com.kenansoylu.bauproject.misc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.kenansoylu.bauproject.data.UserData

class SharedPreferenceManager(val context: Context) {

    val PATH = "preferences"

    fun getData(key: String): String? {
        val sharedPref = this.context.getSharedPreferences(PATH, Context.MODE_PRIVATE)
        return sharedPref.getString(key, "")
    }

    fun saveData(key: String, value: String) {
        val sharedPref = this.context.getSharedPreferences(PATH, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getUser(): UserData? {
        val id = getData("id") ?: ""
        val avatarURI = getData("avatarURI") ?: ""
        val name = getData("name") ?: ""
        // TODO: String to int list
        val scores = listOf(0L)

        return if (id == "") null
        else UserData(id, name, avatarURI, scores)
    }

    fun saveUser(userData: UserData) {
        saveData("id", userData.id)
        saveData("avatarURI", userData.avatarURI)
        saveData("name", userData.name)
        saveData("scores", userData.scores.toString())
    }

    fun deleteUser() {
        saveData("id", "")
        saveData("avatarURI", "")
        saveData("name", "")
        saveData("scores", "")
    }
}