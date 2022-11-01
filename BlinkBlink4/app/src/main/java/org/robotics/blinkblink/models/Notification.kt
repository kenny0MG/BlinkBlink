package org.robotics.blinkblink.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Notification(val uid: String="", val photo: String? = null, val username: String="", val postId: String?=null,val postImage: String?=null, val commentText: String?="",@get:Exclude val id: String ="", val timestemp: Any = ServerValue.TIMESTAMP, val read: Boolean = false, val type: NotificationType=NotificationType.Follow){
    fun fileStemp(): Date = Date(timestemp as Long)
    override fun equals(other: Any?): Boolean {
        return (other as Notification).id == id
    }
}

enum class NotificationType{
    Follow,Like,Comment
}