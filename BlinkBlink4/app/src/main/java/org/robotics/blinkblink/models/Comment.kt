package org.robotics.blinkblink.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.sql.Timestamp
import java.util.*

data class Comment(@get:Exclude val id: String ="", val uid:String="", val username:String="", val text:String="", val photo:String?=null, val timestemp: Any = ServerValue.TIMESTAMP,var proverka:Boolean=false) {
    fun fileStemp(): Date = Date(timestemp as Long)
}