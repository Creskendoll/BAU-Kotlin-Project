package com.kenansoylu.bauproject.data

data class UserData(val id: String, val name: String, val avatarURI: String, val scores: List<Int>) {
    fun serialize() : Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "avatarURI" to avatarURI,
            "scores" to scores
        )
    }
}
