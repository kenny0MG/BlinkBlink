package org.robotics.blinkblink.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import org.robotics.blinkblink.data.FirebaseLiveData
import org.robotics.blinkblink.data.NotificationRepository
import org.robotics.blinkblink.data.common.map
import org.robotics.blinkblink.models.Notification
import org.robotics.blinkblink.utils.NODE_NOTIFICATIONS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database
import org.robotics.blinkblink.utils.toUnit

class FirebaseNotificationRepository: NotificationRepository {
    override fun createNotification(uid: String,notification: Notification): Task<Unit> =
        notificationsRef(uid).push().setValue(notification).toUnit()


    override fun getNotification(uid:String): LiveData<List<Notification>> =
        FirebaseLiveData(notificationsRef(uid)).map{

            it.children.map { it.asNotification()!! }.sortedByDescending { it.fileStemp() }
        }



    override fun setNotificationsRead(uid:String,ids: List<String>, read:Boolean): Task<Unit>{
        val updatesMap =ids.map {"$it/read" to read}.toMap()
        return notificationsRef(uid).updateChildren(updatesMap).toUnit()
    }


    private fun notificationsRef(uid:String) = database.child(NODE_NOTIFICATIONS).child(uid)



    private fun DataSnapshot.asNotification() =
        getValue(Notification::class.java)?.copy(id=key.toString())

}