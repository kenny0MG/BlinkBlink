package org.robotics.blinkblink.Activity.Users

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import org.robotics.blinkblink.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_images_post.*
import kotlinx.android.synthetic.main.user.*
import kotlinx.android.synthetic.main.user.posts
import org.robotics.blinkblink.Activity.ConfidActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.Activity.EditPrifile.EditActivity
import org.robotics.blinkblink.Activity.FollowersFollowing.FollowersActivity
import org.robotics.blinkblink.Activity.FollowersFollowing.FollowingActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.ViewPagerAdapter
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.utils.NODE_IMAGES
import org.robotics.blinkblink.utils.NODE_USERS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database
import org.robotics.blinkblink.views.setupButtonNavigations


class UserActivity : baseActivity() {
    private val TAG = "UserActivity"
    private lateinit var mUser: Users


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.user)
        setupButtonNavigations(2)
        Log.d(TAG, "OnCreate")
        setUpTabsUser()

        buttonuser.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
        settings.setOnClickListener {
            val intent = Intent(this, ConfidActivity::class.java)
            startActivity(intent)
        }

        setupAuthGuard {

        }
        followers.setOnClickListener {
            val intent = Intent(this, FollowersActivity::class.java)
            intent.putExtra("uid", currentUid()!!)
            startActivity(intent)
        }
        followers_text.setOnClickListener {
            val intent = Intent(this, FollowersActivity::class.java)
            intent.putExtra("uid", currentUid()!!)
            startActivity(intent)
        }
        following_text.setOnClickListener {
            val intent = Intent(this, FollowingActivity::class.java)
            intent.putExtra("uid", currentUid()!!)
            startActivity(intent)
        }
        following.setOnClickListener {
            val intent = Intent(this, FollowingActivity::class.java)
            intent.putExtra("uid", currentUid()!!)
            startActivity(intent)
        }

        database.child(NODE_USERS).child(currentUid()!!)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!
                name_profile.setText(mUser.name, TextView.BufferType.EDITABLE)
                usernameusers.setText(mUser.username.toLowerCase().replace(" ", "_"),
                    TextView.BufferType.EDITABLE)
                biousers.setText(mUser.bio, TextView.BufferType.EDITABLE)
                followers.setText(mUser.followers.size.toString(), TextView.BufferType.EDITABLE)
                following.setText(mUser.following.size.toString(), TextView.BufferType.EDITABLE)
                if (!mUser.proverka) {
                    proverkauser.visibility = View.GONE
                } else {
                    proverkauser.visibility = View.VISIBLE

                }
                if(mUser.typeProfile){
                    lock_profile.visibility = View.VISIBLE
                }else{
                    lock_profile.visibility = View.GONE
                }
                Glide.with(this).load(mUser.photo).placeholder(R.drawable.person).into(profile)
            })
        database.child(NODE_IMAGES).child(currentUid()!!)
            .addValueEventListener(Utils.ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
                posts.setText(images.size.toString(), TextView.BufferType.EDITABLE)

            })


    }

    private fun setUpTabsUser() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FeedImageFragment(), "Посты")
        adapter.addFragment(FavoriteFragment(), "Избранное")
        ymsgesrec.adapter = adapter
        tabsuser.setupWithViewPager(ymsgesrec)
        tabsuser.getTabAt(0)!!.setIcon(R.drawable.ic_app6_foreground)
        tabsuser.getTabAt(1)!!.setIcon(R.drawable.ic_favorite6_foreground)

    }



}