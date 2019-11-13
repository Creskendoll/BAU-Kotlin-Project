package com.kenansoylu.bauproject.data

import android.net.Uri

data class PlayerData(val id: Int, val name: String, val avatarURI: Uri, val score: Long, val highScore: Long)