package org.robotics.blinkblink.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import org.robotics.blinkblink.data.FirebaseLiveData
import org.robotics.blinkblink.commons.Event
import org.robotics.blinkblink.commons.EventBus
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.task
import org.robotics.blinkblink.data.FeedPostsRepository
import org.robotics.blinkblink.data.common.map
import org.robotics.blinkblink.models.Comment
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.utils.*

class FirebaseFeedPostsRepository(): FeedPostsRepository {
    private val Currentuid = FirebaseAuth.getInstance().currentUser!!.uid



    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child(NODE_FEEDPOSTS).child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child(NODE_FEEDPOSTS).child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }

    override fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child(NODE_FEEDPOSTS).child(uid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to null }.toMap()
                    database.child(NODE_FEEDPOSTS).child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }
    override fun createComment(postId: String,comment: Comment): Task<Unit> =
        database.child(NODE_COMMENTS).child(postId).push().setValue(comment).toUnit().addOnSuccessListener {
            EventBus.publish(Event.CreateComment(postId,comment))
        }


    override fun getComments(postId: String): LiveData<List<Comment>> =
        FirebaseLiveData(database.child(NODE_COMMENTS).child(postId)).map{
            it.children.map{it.asComment()!!}.sortedByDescending { it.fileStemp() }

        }
    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPosts> =
        FirebaseLiveData(database.child(NODE_FEEDPOSTS).child(uid).child(postId)).map {
            it.asFeedPost()!!
        }
    override fun getFeedPosts(uid: String): LiveData<List<FeedPosts>> =
        FirebaseLiveData(database.child(NODE_FEEDPOSTS).child(uid)).map {
            it.children.map { it.asFeedPost()!! }
        }
    override fun getRecPost(uid: String, postId: String): LiveData<FeedPosts> =
        FirebaseLiveData(database.child(NODE_RECOMMENDATIONPOSTS).child(postId)).map {
            it.asFeedPost()!!
        }

    private fun DataSnapshot.asFeedPost(): FeedPosts? =
        getValue(FeedPosts::class.java)?:FeedPosts()
}