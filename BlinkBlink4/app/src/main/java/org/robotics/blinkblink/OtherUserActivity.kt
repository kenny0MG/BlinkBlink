package org.robotics.blinkblink


import android.content.Intent

import android.os.Bundle

import android.view.View

import android.widget.TextView

import androidx.recyclerview.widget.GridLayoutManager

import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task

import kotlinx.android.synthetic.main.activity_other_user2.*


import org.robotics.blinkblink.Activity.FollowersFollowing.FollowersActivity
import org.robotics.blinkblink.Activity.FollowersFollowing.FollowingActivity
import org.robotics.blinkblink.Activity.ImageListPerson.ImagesActivity
import org.robotics.blinkblink.Activity.SingleChat.AddChatActivity
import org.robotics.blinkblink.Activity.Users.ImagesAdapter
import org.robotics.blinkblink.commons.*
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*
import java.nio.file.Files.size
import java.util.*

class OtherUserActivity : baseActivity(),ImagesAdapter.Listener {
    private val TAG = "UserActivity"
    private lateinit var mUser: Users
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user2)
        closeuser_other.setOnClickListener {
            finish()
        }


        val uid = intent.getStringExtra("uid")


        following_other.setOnClickListener {
            val intent = Intent(this, FollowingActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }

        followers_other.setOnClickListener {
            val intent = Intent(this, FollowersActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
        followers_other_text.setOnClickListener {
            val intent = Intent(this, FollowersActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
        following_other_text.setOnClickListener {
            val intent = Intent(this, FollowingActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }


        database.child(NODE_USERS).child(uid!!).addListenerForSingleValueEvent(
            Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!
                name_otherprofile.setText(mUser.name, TextView.BufferType.EDITABLE)
                usernameusers_other.setText(mUser.username.toLowerCase().replace(" ", "_"),
                    TextView.BufferType.EDITABLE)
                biousers_other.setText(mUser.bio, TextView.BufferType.EDITABLE)
                if (mUser.bio == "") {
                    biousers_other.visibility = View.GONE
                } else {
                    biousers_other.visibility = View.VISIBLE
                }
                if (!mUser.proverka) {
                    proverkauser_other.visibility = View.GONE
                } else {
                    proverkauser_other.visibility = View.VISIBLE

                }
                if (mUser.typeProfile) {
                    database.child(NODE_FOLLOW).child(uid).child(currentUid()!!).child("uid").get().addOnSuccessListener {
                        if(it.exists()){
                            other_images.visibility = View.VISIBLE
                            close_profile.visibility = View.GONE
                            close_profile2.visibility = View.GONE
                            buttun_follow.visibility = View.GONE
                            userunfollow.visibility = View.VISIBLE
                            buttonuser3.visibility = View.VISIBLE
                            buttonuser_sendmsg.visibility = View.VISIBLE
                            lock.visibility = View.GONE
                            followtrue(uid)
                        }
                        else{
                            other_images.visibility = View.GONE
                            close_profile.visibility = View.VISIBLE
                            close_profile2.visibility = View.VISIBLE
                            buttonuser3.visibility = View.GONE
                            userunfollow.visibility = View.GONE

                            buttonuser_sendmsg.visibility = View.GONE
                            lock.visibility = View.VISIBLE
                            followtrueclose(uid)
                        }
                    }

                } else {
                    other_images.visibility = View.VISIBLE
                    close_profile.visibility = View.GONE
                    close_profile2.visibility = View.GONE
                    buttun_unfollow.visibility = View.GONE
                    buttun_follow.visibility = View.GONE
                    userunfollow.visibility = View.VISIBLE
                    buttonuser3.visibility = View.VISIBLE
                    buttonuser_sendmsg.visibility = View.VISIBLE
                    lock.visibility = View.GONE
                    followtrue(uid)
                }



                Glide.with(this).load(mUser.photo).placeholder(R.drawable.person)
                    .into(profile_avatar_other)
                followers_other.setText(mUser.followers.size.toString(),
                    TextView.BufferType.EDITABLE)
                following_other.setText(mUser.following.size.toString(), TextView.BufferType.EDITABLE)
            })
        buttonuser_sendmsg.setOnClickListener {
            val intent = Intent(this, AddChatActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)

        }


        userunfollow.setOnClickListener {
            unfollow(uid)
            buttonuser3.visibility = View.VISIBLE
            userunfollow.visibility = View.GONE

        }
        buttonuser3.setOnClickListener {
            follow(uid)
            buttonuser3.visibility = View.GONE
            userunfollow.visibility = View.VISIBLE

        }
        buttun_follow.setOnClickListener {
            follow(uid)
            buttun_follow.visibility = View.GONE
            buttun_unfollow.visibility = View.VISIBLE

        }
        buttun_unfollow.setOnClickListener {
            unfollow(uid)
            buttun_follow.visibility = View.VISIBLE
            buttun_unfollow.visibility = View.GONE


        }
        other_images.layoutManager = GridLayoutManager(this, 3)
        database.child(NODE_IMAGES).child(uid)
            .addValueEventListener(Utils.ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
                posts_other.setText(images.size.toString(), TextView.BufferType.EDITABLE)
                Collections.reverse(images)
                other_images.adapter = ImagesAdapter(images,this)
            })


    }




    private fun follow(uid: String){
        copyFeedPosts(uid, currentUid()!!)
        database.child("users").child(currentUid()!!).child("following").child(uid).setValue(true)
        database.child("users").child(uid).child("followers").child(currentUid()!!).setValue(true)
        database.child(NODE_FOLLOW).child(currentUid()!!).child(uid).child(NODE_UID).setValue(uid)
        database.child(NODE_FOLLOWERS).child(uid).child(currentUid()!!).child(NODE_UID).setValue(currentUid()!!)


    }
    private fun unfollow(uid:String){
        deleteFeedPosts(uid, currentUid()!!)
        database.child("users").child(currentUid()!!).child("following").removeValue()
        database.child("users").child(uid).child("followers").removeValue()
        database.child(NODE_FOLLOW).child(currentUid()!!).child(uid).child(NODE_UID).removeValue()
        database.child(NODE_FOLLOWERS).child(uid).child(currentUid()!!).child(NODE_UID).removeValue()

    }


    private fun followtrue(uid: String) {
       database.child(NODE_FOLLOW).child(currentUid()!!).child(uid).child("uid").get()
            .addOnSuccessListener {
            if (it.exists()) {
                buttonuser3.visibility = View.GONE
                userunfollow.visibility = View.VISIBLE


            } else {
                userunfollow.visibility = View.GONE
                buttonuser3.visibility = View.VISIBLE



            }
        }
    }
    private fun followtrueclose(uid: String) {
       database.child(NODE_FOLLOW).child(currentUid()!!).child(uid).child("uid").get()
       .addOnSuccessListener  {
            if (it.exists()) {
                buttun_follow.visibility = View.GONE
                buttun_unfollow.visibility = View.VISIBLE



            } else {
                buttun_follow.visibility = View.VISIBLE
                buttun_unfollow.visibility = View.GONE




            }
        }
    }
     private fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }
    private fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(uid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to null }.toMap()
                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }

    override fun openItem(image: String) {
        val uid = intent.getStringExtra("uid")
        val intent = Intent(this, ImagesActivity ::class.java)
        intent.putExtra("image",image)
        intent.putExtra("uid",uid)


       startActivities(arrayOf(intent))
    }
}







