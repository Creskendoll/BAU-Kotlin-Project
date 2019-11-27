package com.kenansoylu.bauproject.services

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.kenansoylu.bauproject.data.UserData
import com.kenansoylu.bauproject.misc.SharedPreferenceManager
import java.lang.Exception

class UserService(context: Context) {
    val spManager = SharedPreferenceManager(context)
    val DB_NAME = "players"
    val db = FirebaseFirestore.getInstance()

    fun getAllUsers(then: (List<UserData>) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).get().addOnSuccessListener { result ->
            val players = result.map {
                UserData(
                    it.id,
                    it.data["name"] as String,
                    it.data["avatarURI"] as String,
                    it.data["scores"] as MutableList<Long>
                )
            }
            then(players)
        }.addOnFailureListener { exception ->
            catch(exception)
        }
    }

    fun getUserByID(userID: String, then: (UserData) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).document(userID).get().addOnSuccessListener {
            val userData = UserData(
                userID,
                it["name"] as String,
                it["avatarURI"] as String,
                it["scores"] as MutableList<Long>
            )

            then(userData)
        }.addOnFailureListener(catch)
    }

    fun getUser(user: FirebaseUser, then: (UserData) -> Unit, catch: (Exception) -> Unit) {
        getUserByID(user.uid, then, catch)
    }

    fun addUser(userData: UserData, then: (Void?) -> Unit, catch: (Exception) -> Unit) {
        db.collection(DB_NAME).document(userData.id).set(userData.serialize())
            .addOnSuccessListener(then)
            .addOnFailureListener(catch)
    }

    fun updateUser(
        oldUserData: UserData,
        newUserData: UserData,
        then: (Void?) -> Unit,
        catch: (Exception) -> Unit
    ) {
        spManager.deleteUser()
        spManager.saveUser(newUserData)
        db.collection(DB_NAME).document(oldUserData.id).set(newUserData.serialize())
            .addOnSuccessListener(then)
            .addOnFailureListener(catch)
    }
}