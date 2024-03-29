package org.robotics.blinkblink.Activity.Notification

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import org.robotics.blinkblink.commons.Event
import org.robotics.blinkblink.commons.EventBus
import org.robotics.blinkblink.data.FeedPostsRepository
import org.robotics.blinkblink.data.NotificationRepository
import org.robotics.blinkblink.data.UsersRepository
import org.robotics.blinkblink.data.common.observeFirstNotNull
import org.robotics.blinkblink.data.common.zip
import org.robotics.blinkblink.models.Notification
import org.robotics.blinkblink.models.NotificationType

class NotificationCreator(private val notificationsRepo: NotificationRepository,
                          private val usersRepo: UsersRepository,
                          private val feedPostsRepo: FeedPostsRepository
) : LifecycleOwner {
    private val lifecircleregister = LifecycleRegistry(this)
    init {
        lifecircleregister.markState(Lifecycle.State.CREATED);
        lifecircleregister.markState(Lifecycle.State.STARTED);

        EventBus.events.observe(this, Observer { it?.let {event ->
            when(event){
                is Event.CreateFollow -> {
                    getUser(event.fromUid).observeFirstNotNull(this) { user ->
                        val notification = Notification(
                            uid = user.uid,
                            username = user.username,
                            photo = user.photo,
                            type = NotificationType.Follow)
                        notificationsRepo.createNotification(event.toUid, notification)
                            .addOnFailureListener {
                                Log.d(TAG, "Failed to create notification", it)
                            }
                }
                }
                is Event.CreateComment-> {
                    feedPostsRepo.getFeedPost(uid = event.comment.uid, postId = event.postId)
                        .observeFirstNotNull(this) { post ->
                            val notification = Notification(
                                uid = event.comment.uid,
                                username = event.comment.username,
                                photo = event.comment.photo,
                                postId = event.postId,
                                postImage = post.image,
                                commentText = event.comment.text,
                                type = NotificationType.Comment)
                            notificationsRepo.createNotification(post.uid, notification)
                                .addOnFailureListener {
                                    Log.d(TAG, "Failed to create notification", it)
                                }
                        }
                }
                is Event.CreateCommentRec-> {
                    feedPostsRepo.getRecPost(uid = event.comment.uid, postId = event.postId)
                        .observeFirstNotNull(this) { post ->
                            val notification = Notification(
                                uid = event.comment.uid,
                                username = event.comment.username,
                                photo = event.comment.photo,
                                postId = event.postId,
                                postImage = post.image,
                                commentText = event.comment.text,
                                type = NotificationType.Comment)
                            notificationsRepo.createNotification(post.uid, notification)
                                .addOnFailureListener {
                                    Log.d(TAG, "Failed to create notification", it)
                                }
                        }
                }

//
                is Event.CreateLike ->  {
                    val userData = usersRepo.getUser()
                    val postData = feedPostsRepo.getFeedPost(uid = event.uid, postId = event.postId)

                    userData.zip(postData).observeFirstNotNull(this) { (user, post) ->
                        val notification = Notification(
                            uid = user.uid,
                            username = user.username,
                            photo = user.photo,
                            postId = post.id,
                            postImage = post.image,
                            type = NotificationType.Like)
                        notificationsRepo.createNotification(post.uid, notification)
                            .addOnFailureListener {
                                Log.d(TAG, "Failed to create notification", it)
                            }
                    }

                }




            }
        } })
    }

    private fun getUser(uid: String) = usersRepo.getUser()


    override fun getLifecycle(): Lifecycle = lifecircleregister
}



