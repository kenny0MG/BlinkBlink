package org.robotics.blinkblink.commons

import android.app.Application
import org.robotics.blinkblink.Activity.Notification.NotificationCreator
import org.robotics.blinkblink.data.firebase.FirebaseFeedPostsRepository
import org.robotics.blinkblink.data.firebase.FirebaseNotificationRepository
import org.robotics.blinkblink.data.firebase.FirebaseUsersRepository

class BlinkApp:Application() {
    val usersRepo by lazy { FirebaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val notificationsRepo by lazy { FirebaseNotificationRepository() }

    override fun onCreate() {
        super.onCreate()
        NotificationCreator(notificationsRepo, usersRepo, feedPostsRepo)
    }
}