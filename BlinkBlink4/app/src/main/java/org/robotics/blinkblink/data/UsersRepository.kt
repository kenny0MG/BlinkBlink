package org.robotics.blinkblink.data

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import org.robotics.blinkblink.models.Users

interface UsersRepository {
    fun getUsers(): LiveData<List<Users>>
    fun currentUid(): String?
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollower(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollower(fromUid: String, toUid: String): Task<Unit>
    fun getUser(): LiveData<Users>
    fun updateUsersProfile(currentUser: Users, newUser: Users): Task<Unit>
    fun searchPosts(text: String): LiveData<List<Users>>

    fun getUsers(uid: String): LiveData<List<Users>>
}