package com.kenansoylu.bauproject.services

import com.google.firebase.firestore.FirebaseFirestore
import com.kenansoylu.bauproject.data.PlayerData
import java.lang.Exception

class UserService {
    val DB_NAME = "players"
    val db = FirebaseFirestore.getInstance()

    fun getAllUsers(then: (List<PlayerData>) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).get().addOnSuccessListener { result ->
            val players = result.map { PlayerData(it.id, it.data["name"] as String, it.data["avatar"] as String, it.data["scores"] as List<Int>) }
            then(players)
        }.addOnFailureListener {exception ->
            catch(exception)
        }
    }

    fun addUser(userData : PlayerData) {

    }
}