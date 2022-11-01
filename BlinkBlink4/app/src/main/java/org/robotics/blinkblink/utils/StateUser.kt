package org.robotics.blinkblink.utils

import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.edit.*
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.models.Users


private lateinit var mUser: Users
enum class StateUser(val state:String) {


    ONLINE("в сети"),
    OFFLINE("не в сети"),
    TYPING("печатает...");
    companion object{

        fun updateState(appStates: StateUser){

            database.child(NODE_USERS).child(currentUid()!!).addListenerForSingleValueEvent(
                Utils.ValueEventListenerAdapter {
                mUser = it.getValue(Users::class.java)!!


            })



            database.child(NODE_USERS).child(currentUid()!!).child("state")
                .setValue(appStates.state ).addOnSuccessListener {
                    mUser.state = appStates.state }

        }
    }
}


