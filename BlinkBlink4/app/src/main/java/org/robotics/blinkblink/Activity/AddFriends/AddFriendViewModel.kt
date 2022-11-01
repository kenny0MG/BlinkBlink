package org.robotics.blinkblink.Activity.AddFriends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.robotics.blinkblink.data.FirebaseLiveData
import org.robotics.blinkblink.data.FeedPostsRepository
import org.robotics.blinkblink.data.UsersRepository
import org.robotics.blinkblink.data.common.asUsers
import org.robotics.blinkblink.data.common.map
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.NODE_FOLLOW
import org.robotics.blinkblink.utils.NODE_FOLLOWERS
import org.robotics.blinkblink.utils.NODE_UID

class AddFriendViewModel(private val onFailureListener: OnFailureListener, private val usersRepository: UsersRepository, private val feedpostsRepository: FeedPostsRepository): ViewModel() {

    private val Currentuid = FirebaseAuth.getInstance().currentUser!!.uid
    private val database = FirebaseDatabase.getInstance().reference
    private val searchtext=MutableLiveData<String>()
    private val _userAndFriends =
        FirebaseLiveData(FirebaseDatabase.getInstance().reference.child("users"))
    val userAndFriends: LiveData<Pair<Users, List<Users>>> =
        usersRepository.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == usersRepository.currentUid()

            }

            userList.first() to otherUsersList
        }
    val posts: LiveData<List<Users>> = Transformations.switchMap(searchtext) { text ->
        usersRepository.searchPosts(text)
    }




    //осущевствление подписки и одписки
    fun setFollow(currentUid: String,uid: String, follow: Boolean,): Task<Void> {
       return if(follow){
            Tasks.whenAll(usersRepository.addFollow(usersRepository.currentUid()!!,uid),
                usersRepository.addFollower(usersRepository.currentUid()!!,uid),
                database.child(NODE_FOLLOW).child(currentUid).child(uid).child(NODE_UID).setValue(uid),
                database.child(NODE_FOLLOWERS).child(uid).child(currentUid).child(NODE_UID).setValue(currentUid),
                feedpostsRepository.copyFeedPosts(postsAuthorUid = uid,uid =currentUid )
            )
        }else{
            Tasks.whenAll(usersRepository.deleteFollow(usersRepository.currentUid()!!,uid),
                usersRepository.deleteFollower(usersRepository.currentUid()!!,uid),
                database.child(NODE_FOLLOW).child(currentUid).child(uid).child(NODE_UID).removeValue() ,
                database.child(NODE_FOLLOWERS).child(uid).child(currentUid).child(NODE_UID).removeValue(),

                feedpostsRepository.deleteFeedPosts(postsAuthorUid = uid,uid =currentUid )
            )
        }.addOnFailureListener(onFailureListener)
    }

    fun setSearchText(text: String) {
        searchtext.value=text
    }

}



