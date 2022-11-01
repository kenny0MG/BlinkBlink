package org.robotics.blinkblink.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import org.robotics.blinkblink.data.FirebaseLiveData
import org.robotics.blinkblink.commons.Event
import org.robotics.blinkblink.commons.EventBus
import org.robotics.blinkblink.data.UsersRepository
import org.robotics.blinkblink.data.common.asUser
import org.robotics.blinkblink.data.common.map
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*

class FirebaseUsersRepository: UsersRepository {
    override fun getUsers(): LiveData<List<Users>> = getUsers(currentUid()!!)
    override fun getUsers(uid: String): LiveData<List<Users>> =
        database.child(NODE_USERS).liveData().map { it.children.map { it.asUser()!! }}
    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
        getFollowingRef(fromUid, toUid).setValue(true).toUnit().addOnSuccessListener {
            EventBus.publish(Event.CreateFollow(fromUid, toUid))
        }


    override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
        getFollowingRef(fromUid, toUid).removeValue().toUnit()

    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
        getFollowersRef(fromUid, toUid).setValue(true).toUnit()

    override fun deleteFollower(fromUid: String, toUid: String): Task<Unit> =
        getFollowersRef(fromUid, toUid).removeValue().toUnit()
    private fun getFollowingRef(fromUid: String, toUid: String) =
        database.child(NODE_USERS).child(fromUid).child(NODE_FOLLOW).child(toUid)

    private fun getFollowersRef(fromUid: String, toUid: String) =
        database.child(NODE_USERS).child(toUid).child(NODE_FOLLOWERS).child(fromUid)

    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid

    override fun getUser(): LiveData<Users> =
        database.child(NODE_USERS).child(currentUid()!!).liveData().map {
            it.asUser()!!
        }
    override fun searchPosts(text: String): LiveData<List<Users>> {
        val reference = database.child(NODE_USERS).child(currentUid()!!)
        val query = if (text.isEmpty()) {
            reference
        } else {
            database.child(NODE_USERS).orderByChild("username")
                .startAt(text.toLowerCase()).endAt("${text.toLowerCase()}\\uf8ff")!!
        }
        return FirebaseLiveData(query).map {
            it.children.map { it.asUser()!!}
        }
    }
    override fun updateUsersProfile(currentUser: Users, newUser: Users): Task<Unit> {

        val updatesMap = mutableMapOf<String, Any>()
        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["username"] =
            newUser.username.toLowerCase().replace(" ", "_")
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
            //.addOnCompleteListener{

//            if(it.isSuccessful){
//                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            else{
//                Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT).show()
//
//            }
       // }

}
}


