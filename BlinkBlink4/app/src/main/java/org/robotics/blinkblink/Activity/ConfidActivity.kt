package org.robotics.blinkblink.Activity

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import org.robotics.blinkblink.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_confid.*
import kotlinx.android.synthetic.main.activity_other_user2.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.utils.CHILD_TYPE_PROFILE
import org.robotics.blinkblink.utils.NODE_USERS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database


class ConfidActivity : baseActivity() {
    private lateinit var mUser: Users
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confid)
        close.setOnClickListener{
            finish()
        }

        signout.setOnClickListener{
            finish()
            mAuth.signOut()
        }
        setupAuthGuard {

        }
        mAuth = FirebaseAuth.getInstance()
        database.child(NODE_USERS).child(currentUid()!!).addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

            mUser=it.getValue(Users::class.java)!!

        })
        database.child(NODE_USERS).child(currentUid()!!).child(CHILD_TYPE_PROFILE).get().addOnSuccessListener {
            if(it.exists()){

                switch1.isChecked = true

            }
        }
        switch1.setOnCheckedChangeListener{ _, onSwitch ->
            if(onSwitch){
                database.child(NODE_USERS).child(currentUid()!!).child(CHILD_TYPE_PROFILE).setValue(true)
            }else{
                database.child(NODE_USERS).child(currentUid()!!).child(CHILD_TYPE_PROFILE).removeValue()

            }
        }

    }

    private fun updateUsers(user:Users){
        val updatesMap = mutableMapOf<String,Any>()

        if(user.email != mUser.email) updatesMap["email"] = user.email
        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun validate1(user:Users):String?=
        when{
            user.email.isEmpty() -> "Please enter email"


            else -> null
        }
}