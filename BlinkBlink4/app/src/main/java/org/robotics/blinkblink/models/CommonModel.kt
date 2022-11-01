package org.robotics.blinkblink.models

import com.google.firebase.database.ServerValue
import java.util.*


data class CommonModel(
        val id: String = "",
        var text: String = "",
        var postId: String = "",
        var userId: String = "",
        var author: String = "",
        var imagePosts: String = "",
        var userPhoto: String = "",
        var type: String = "",
        var from: String = "",
        var timeStamp: Any = "",
        var read: Boolean = false,
        val photo: String?=""

    )
{


    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}