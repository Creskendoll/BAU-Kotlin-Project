package com.kenansoylu.bauproject.misc

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    fun getUser(): UserData? {
        val id = getData("id") ?: ""
        val avatarURI = getData("avatarURI") ?: ""
        val name = getData("name") ?: ""
        // TODO: String to int list
        val scoresTxt = getData("scores") ?: ""
        val scores = Gson().fromJson<MutableList<Long>>(scoresTxt)

        return if (id == "") null
        else UserData(id, name, avatarURI, scores)
    }

    fun saveUser(userData: UserData) {
        saveData("id", userData.id)
        saveData("avatarURI", userData.avatarURI)
        saveData("name", userData.name)
        val scoresStr = Gson().toJson(userData.scores)
        saveData("scores", scoresStr)
    }

    fun deleteUser() {
        saveData("id", "")
        saveData("avatarURI", "")
        saveData("name", "")
        saveData("scores", "")
    }
}