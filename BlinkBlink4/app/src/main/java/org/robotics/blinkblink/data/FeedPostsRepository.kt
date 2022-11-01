package org.robotics.blinkblink.data

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import org.robotics.blinkblink.models.Comment
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users

interface FeedPostsRepository {

    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun createComment(postId: String, comment: Comment): Task<Unit>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPosts>
    fun getFeedPosts(uid: String): LiveData<List<FeedPosts>>
    fun getRecPost(uid: String, postId: String): LiveData<FeedPosts>
}