package org.robotics.blinkblink.Activity.Main

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.viewpager2.widget.ViewPager2
import org.robotics.blinkblink.models.Users
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_main.*
import org.robotics.blinkblink.*
import org.robotics.blinkblink.Activity.Notification.NotificationActivity
import org.robotics.blinkblink.Activity.AddFriends.addFriendsActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.utils.*
import org.robotics.blinkblink.views.setupButtonNavigations


class MainActivity : baseActivity() {
    private val TAG = "ChatActivity"
    //private lateinit var adapter: ImageViewPagerAdapter
    private lateinit var ViewPager: ViewPager2
    private lateinit var mUser: Users
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    //private lateinit var mAdapter: FeedAdapter
    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupButtonNavigations(0)
        mAuth = FirebaseAuth.getInstance()

        Log.d(TAG, "OnCreate")

        var bottomfrSheets = bottomSheets()

        setupAuthGuard {
            checkMainPosts()
        }
        posts.setOnClickListener {

            bottomfrSheets.show(supportFragmentManager, "")
        }

        search.setOnClickListener{
            val intent = Intent(this, addFriendsActivity::class.java)
            startActivity(intent)

        }

        bell.setOnClickListener {

            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)

        }

    }

   fun setUpTabs(){
        shimmer_main.stopShimmer()
        shimmer_main.visibility = View.GONE
        main_text_progress.visibility = View.GONE
        main_text.visibility = View.VISIBLE
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(RecommendationFragment(),"Лента")
        adapter.addFragment(FeedPostFragment(),"Подписки")
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
        tabs.getTabAt(0)!!
        tabs.getTabAt(1)!!

    }
    private fun checkMainPosts() {

        database.child(NODE_RECOMMENDATIONPOSTS).child(currentUid()!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (shimmer_main != null) {
                        setUpTabs()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "$error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    override fun onStart() {
        super.onStart()
        StateUser.updateState(StateUser.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        StateUser.updateState(StateUser.OFFLINE)
    }

}

