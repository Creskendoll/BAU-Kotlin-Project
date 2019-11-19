package com.kenansoylu.bauproject.services

import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class ResourceService {
    private val storage = FirebaseStorage.getInstance()

    fun writeData(
        path: String,
        data: ByteArray,
        then: (String) -> Unit,
        catch: (Exception?) -> Unit
    ) {
        val ref = storage.getReference(path)
        // TODO : Add progress
        val uploadTask = ref.putBytes(data)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                then(task.result.toString())
            } else {
                catch(task.exception)
            }

        }
    }

    fun getData(uri: String, then: (ByteArray) -> Unit, catch: (Exception?) -> Unit) {
        val fileRef = storage.getReferenceFromUrl(uri)

        val ONE_MEGABYTE: Long = 1024 * 1024
        fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            then(it)
        }.addOnFailureListener {
            catch(it)
        }
    }
}