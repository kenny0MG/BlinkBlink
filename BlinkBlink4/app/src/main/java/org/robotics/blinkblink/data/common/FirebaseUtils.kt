package org.robotics.blinkblink.data.common

import com.google.firebase.database.DataSnapshot
import org.robotics.blinkblink.models.Users


fun DataSnapshot.asUser(): Users?=
    getValue(Users::class.java)!!.copy(uid = key.toString())

fun DataSnapshot.asUsers(): Users?=
    getValue(Users::class.java)!!.copy(uid = key.toString())