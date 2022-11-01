package org.robotics.blinkblink.data

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import org.robotics.blinkblink.models.Notification

interface NotificationRepository {
    fun createNotification(notification: String, uid: Notification): Task<Unit>
    fun getNotification(uid:String): LiveData<List<Notification>>
    fun setNotificationsRead(uid:String,ids: List<String>, read:Boolean):Task<Unit>
}
