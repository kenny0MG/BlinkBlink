package org.robotics.blinkblink.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPosts(val uid: String ="", val username: String="", val photo: String?="",
                     val image:String="", val commentsCount:Int=0, val caption:String="",
                     val comments:List<Comment> = emptyList(), val timestemp:Any= ServerValue.TIMESTAMP,
                     val name:String="", @get:Exclude val id:String = "",var proverka:Boolean=false,
                     val type: Int = 1,)
{
    fun fileStemp(): Date = Date(timestemp as Long)
}