package com.kenansoylu.bauproject.services

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kenansoylu.bauproject.data.UserData
import java.lang.Exception

class UserService {
    val DB_NAME = "players"
    val db = FirebaseFirestore.getInstance()

    fun getAllUsers(then: (List<UserData>) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).get().addOnSuccessListener { result ->
            val players = result.map {
                //                Log.d("RESULT", it.data.entries.toString())
                UserData(
                    it.data["id"] as String,
                    it.data["name"] as String,
                    it.data["avatarURI"] as String,
                    it.data["scores"] as List<Int>
                )
            }
            then(players)
        }.addOnFailureListener { exception ->
            catch(exception)
        }
    }

    fun getUser(user: FirebaseUser, then: (UserData) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).get().addOnSuccessListener { result ->
            val docRef = result.first { it["id"] == user.uid }
            val userData = UserData(
                docRef["id"] as String,
                docRef["name"] as String,
                docRef["avatarURI"] as String,
                docRef["scores"] as List<Int>
            )
            then(userData)
        }.addOnFailureListener(catch)
    }

    fun addUser(userData: UserData, then: (DocumentReference) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).add(userData.serialize()).addOnSuccessListener(then)
            .addOnFailureListener(catch)
    }
}