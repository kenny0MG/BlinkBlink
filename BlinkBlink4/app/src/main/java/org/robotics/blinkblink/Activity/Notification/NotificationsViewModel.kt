package org.robotics.blinkblink.Activity.Notification

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import org.robotics.blinkblink.commons.BaseViewModel
import org.robotics.blinkblink.data.NotificationRepository
import org.robotics.blinkblink.models.CommonModel
import org.robotics.blinkblink.models.Notification
import org.robotics.blinkblink.models.Users

class NotificationsViewModel(private val notificationsRepo: NotificationRepository,onFailureListener: OnFailureListener): BaseViewModel(onFailureListener) {
    lateinit var notifications: LiveData<List<Notification>>
    private lateinit var uid: String
    fun init(uid: String) {
        this.uid = uid

        notifications = notificationsRepo.getNotification(uid)
    }

    fun setNotificationsRead(notifications: List<Notification>) {
        val ids = notifications.filter { !it.read }.map{it.id}
        if(ids.isNotEmpty()){
            notificationsRepo.setNotificationsRead(uid,ids,true).addOnFailureListener(onFailureListener)

        }
    }

}