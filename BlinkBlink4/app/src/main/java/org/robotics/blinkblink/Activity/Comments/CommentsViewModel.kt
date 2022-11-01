package org.robotics.blinkblink.Activity.Comments

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import org.robotics.blinkblink.commons.BaseViewModel
import org.robotics.blinkblink.data.FeedPostsRepository
import org.robotics.blinkblink.data.UsersRepository
import org.robotics.blinkblink.models.Comment
import org.robotics.blinkblink.models.Users


class CommentsViewModel(private val feedPostsRepo: FeedPostsRepository, onFailureListener: OnFailureListener, usersRepo: UsersRepository): BaseViewModel(onFailureListener) {
    private lateinit var postId: String
    lateinit var comments: LiveData<List<Comment>>
    val user: LiveData<Users> = usersRepo.getUser()
    fun init(postId:String){
        this.postId = postId
        comments = feedPostsRepo.getComments(postId)
    }

    fun createComment(text: String, user: Users){
       val comment = Comment(
           uid = user.uid,
           username = user.username,
           photo = user.photo,
           text = text)

        feedPostsRepo.createComment(postId,comment).addOnFailureListener { onFailureListener }

    }

}