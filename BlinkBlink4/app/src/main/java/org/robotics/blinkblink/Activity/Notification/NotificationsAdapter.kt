package org.robotics.blinkblink.Activity.Notification

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_notification.view.*
import kotlinx.android.synthetic.main.add_friends_item.view.*
import kotlinx.android.synthetic.main.notifications_item.view.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.*
import org.robotics.blinkblink.Activity.Main.SimpleCallback
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.OtherUserActivity
import org.robotics.blinkblink.models.Notification
import org.robotics.blinkblink.models.NotificationType
import org.robotics.blinkblink.utils.currentUid

class NotificationsAdapter: RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    private var notification= listOf<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notifications_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notification[position]
        with(holder.view){

                avatar_not.loadUserPhoto(notification.photo)
                val notificationText =
                    when (notification.type) {
                        NotificationType.Comment -> "Commented: ${notification.commentText}"
                        NotificationType.Like -> "Liked your post."
                        NotificationType.Follow -> "Started following you."
                    }
                text_not.setCaptionText2(notification.username,
                    notificationText,
                    notification.fileStemp())
                text_not.setOnClickListener{
                    if(notification.uid == currentUid()!!){
                        val intent = Intent(context, UserActivity::class.java)
                        context.startActivities(arrayOf(intent))
                    }else{
                        val intent = Intent(context, OtherUserActivity ::class.java)
                        intent.putExtra("uid",notification.uid)


                        context.startActivities(arrayOf(intent))
                    }
                }
                post_image.loadImageOrHide(notification.postImage)
                avatar_not.setOnClickListener{
                    if(notification.uid == currentUid()!!){
                        val intent = Intent(context, UserActivity::class.java)
                        context.startActivities(arrayOf(intent))
                    }else{
                        val intent = Intent(context, OtherUserActivity ::class.java)
                        intent.putExtra("uid",notification.uid)


                        context.startActivities(arrayOf(intent))
                    }
                }




        }
    }

    override fun getItemCount(): Int = notification.size

    fun updateNotifications(newNotifications: List<Notification>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.notification, newNotifications) {it.id})
        this.notification = newNotifications
        diffResult.dispatchUpdatesTo(this)
    }
}