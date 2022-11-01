package org.robotics.blinkblink.models

import com.google.firebase.database.Exclude

data class Users(val photo: String?="", val name: String ="", val username:String = "",
                 val bio: String="", val email: String="", val uid:String="",
                 val following: Map<String,Boolean> = emptyMap(), val followers: Map<String,Boolean> = emptyMap(),
                 var state:String ="",@get:Exclude var lastmsg:String ="", var proverka:Boolean=false,
                 var choice:Boolean = false,
                 var type: String = "",var id: String = "",@get:Exclude var timeStampUser: Any = "",var read: Boolean = false,var from:String ="",var typeProfile: Boolean = false) {
}