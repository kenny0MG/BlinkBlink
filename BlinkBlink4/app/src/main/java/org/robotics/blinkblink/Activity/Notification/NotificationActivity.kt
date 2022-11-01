package org.robotics.blinkblink.Activity.Notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_notification.view.*
import kotlinx.android.synthetic.main.fragment_feed_post.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.utils.NODE_NOTIFICATIONS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database


class NotificationActivity : baseActivity() {
    private lateinit var mAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        close.setOnClickListener{
            finish()
        }

        setupAuthGuard {uid ->
            mAdapter =NotificationsAdapter()
            not_recycle.layoutManager = LinearLayoutManager(this)

            not_recycle.adapter = mAdapter
            val viewModel = initViewModel<NotificationsViewModel>()
            viewModel.init(uid)
            checkNotificationProgressBar()
            viewModel.notifications.observe(this, Observer{it?.let{

                mAdapter.updateNotifications(it)
                viewModel.setNotificationsRead(it)
            }})


        }
    }

    private fun checkNotificationProgressBar() {


        database.child(NODE_NOTIFICATIONS).child(currentUid()!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (progress_notification != null) {
                        progress_notification.visibility = View.GONE

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "$error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}