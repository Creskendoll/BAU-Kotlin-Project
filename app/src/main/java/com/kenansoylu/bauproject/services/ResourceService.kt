package com.kenansoylu.bauproject.services

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import java.lang.Exception

class ResourceService {
    private val storage = FirebaseStorage.getInstance()

    fun writeData(
        path: String,
        data: ByteArray,
        caption: String? = "",
        then: (String) -> Unit,
        catch: (Exception?) -> Unit
    ) {
        val ref = storage.getReference(path)
        val meta = StorageMetadata.Builder().setCustomMetadata("caption", caption).build()
        // TODO : Add progress
        val uploadTask = ref.putBytes(data, meta)

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