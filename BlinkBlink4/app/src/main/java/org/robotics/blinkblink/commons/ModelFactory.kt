package org.robotics.blinkblink.commons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import org.robotics.blinkblink.Activity.Comments.CommentsViewModel
import org.robotics.blinkblink.Activity.Notification.NotificationsViewModel
import org.robotics.blinkblink.Activity.AddFriends.AddFriendViewModel
import org.robotics.blinkblink.data.firebase.FirebaseFeedPostsRepository
import org.robotics.blinkblink.Activity.EditPrifile.EditProfileViewModel
import org.robotics.blinkblink.Activity.Login.LoginViewModel
import org.robotics.blinkblink.data.firebase.FirebaseUsersRepository
import org.robotics.blinkblink.Activity.Notification.NotificationCreator

@Suppress("UNCHECKED_CAST")
class ModelFactory(private val app: BlinkApp,
                   private val commonViewModel: CommonViewModel,
                   private val onFailureListener: OnFailureListener): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val usersRepo = app.usersRepo
        val feedPostsRepo = app.feedPostsRepo
        val notificationsRepo = app.notificationsRepo
        NotificationCreator(notificationsRepo, usersRepo, feedPostsRepo)

        if (modelClass.isAssignableFrom(AddFriendViewModel::class.java)) {
            return AddFriendViewModel(onFailureListener,
                FirebaseUsersRepository(), FirebaseFeedPostsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(onFailureListener, FirebaseUsersRepository()) as T
        } else if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(feedPostsRepo, onFailureListener, usersRepo) as T
        } else if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(notificationsRepo, onFailureListener) as T
       }
//        else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
//            return LoginViewModel(FirebaseAuthManager(), app, commonViewModel, onFailureListener) as T
//        }
            else {
            error("Ucknown view model class ${modelClass}")
        }
    }
}

