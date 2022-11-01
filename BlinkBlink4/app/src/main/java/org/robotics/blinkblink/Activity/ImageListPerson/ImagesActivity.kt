package org.robotics.blinkblink.Activity.ImageListPerson;

import android.content.Context
import android.content.Intent
import android.os.Build

import android.os.Bundle;
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_images.*
import org.robotics.blinkblink.Activity.Comments.CommentsActivity
import org.robotics.blinkblink.Activity.Main.FeedAdapter
import org.robotics.blinkblink.Activity.Main.FeedPostFavorite
import org.robotics.blinkblink.Activity.Main.FeedPostLikes
import org.robotics.blinkblink.Activity.SendPosts.SendPostActivity
import org.robotics.blinkblink.BottomMoreFragment

import org.robotics.blinkblink.R;
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.Event
import org.robotics.blinkblink.commons.EventBus
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*

private lateinit var mAdapter: FeedAdapter
private lateinit var mUser: Users
private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()
private var mFavoriteListeners: Map<String, ValueEventListener> = emptyMap()
class ImagesActivity() :baseActivity(), FeedAdapter.Listener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        close_image_activity.setOnClickListener {
            finish()
        }
        mAdapter =
            FeedAdapter(this)
        val image = intent.getStringExtra("image")
        val uid = intent.getStringExtra("uid")

        database.child(NODE_USERS).child(uid!!)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!
                name_images_activity.text = mUser.username.toUpperCase()
            })

        Log.d(TAG, "OnImage ${image}")
        database.child(NODE_POST).child(uid)
            .addValueEventListener(Utils.ValueEventListenerAdapter {

                val posts = it.children.map {
                    it.getValue(FeedPosts::class.java)?.copy(id = it.key.toString())!!
                }.sortedByDescending { it.fileStemp() }
                mAdapter.updatePosts(posts)



                recycler_view_image_activity.setHasFixedSize(true)
                recycler_view_image_activity.isNestedScrollingEnabled = false
                recycler_view_image_activity.adapter = mAdapter

                recycler_view_image_activity.layoutManager = LinearLayoutManager(this)
                recycler_view_image_activity.layoutManager!!.scrollToPosition(image!!.toInt());

                Log.d(TAG,"feedPosts: ${posts.joinToString("\n", "\n")}")




            })
    }
    fun vibratePhone() {
        val vibrator = this?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(20)
        }
    }


    override fun toogleLike(postId: String) {

        val reference = database.child(NODE_LIKES).child(postId).child(currentUid()!!)
        reference.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
            if (it.exists()) {
                reference.removeValue()
            } else {
                vibratePhone()
                reference.setValue(true).addOnSuccessListener {

                    EventBus.publish(Event.CreateLike(postId, currentUid()!!))
                }
            }
        })
    }

    override fun loadlikes(postId: String, position: Int) {
        fun createListener() = database.child(NODE_LIKES).child(postId).addValueEventListener(
            Utils.ValueEventListenerAdapter {
                val userLikes = it.children.map { it.key }.toSet()
                val postLikes =
                    FeedPostLikes(userLikes.size,
                        userLikes.contains(currentUid()!!))
                mAdapter.updatePostLikes(position, postLikes)
            })
        if (mLikesListeners[postId] == null) {
            mLikesListeners += (postId to createListener())
        }

    }

    override fun openComm(postId: String) {
        openComments(postId)
    }


    override fun tooglefavorite(postId: String,image:String) {
        val reference = database.child(NODE_FAVORITE).child(postId).child(currentUid()!!)
        val referenceImageFavorite = database.child(NODE_FAVORITE_IMAGE).child(currentUid()!!).child(postId)

        reference.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
            if (it.exists()) {
                referenceImageFavorite.removeValue()
                reference.removeValue()
            } else {
                referenceImageFavorite.setValue(image)
                reference.setValue(true)
            }
        })
    }


    override fun loadfavorite(postId: String, position: Int) {
        fun createListener() = database.child(NODE_FAVORITE).child(postId).addValueEventListener(
            Utils.ValueEventListenerAdapter {
                val userIzbr = it.children.map { it.key }.toSet()
                val postIzbr =
                    FeedPostFavorite(userIzbr.size,
                        userIzbr.contains(currentUid()!!))
                mAdapter.updatePostIzbr(position, postIzbr)
            })
        if (mFavoriteListeners[postId] == null) {
            mFavoriteListeners += (postId to createListener())
        }
    }

    override fun openbottom(postId: String,uid:String) {
        vibratePhone()
        var bottomfrSheets = BottomMoreFragment(uid,postId)
        val fragmentManager =(this as FragmentActivity).supportFragmentManager
        fragmentManager?.let{bottomfrSheets.show(it,"")}
    }


    override fun openchats(postId: String,userId: String,username:String,postImage:String,userImage:String) {
        vibratePhone()
        val intent = Intent(this, SendPostActivity::class.java)
        intent.putExtra("postId",postId)
        intent.putExtra("userId",userId)
        intent.putExtra("username",username)
        intent.putExtra("postImage",postImage)
        intent.putExtra("userImage",userImage)
       startActivities(arrayOf(intent))
    }


    fun openComments(postId: String) {
        CommentsActivity.start(this,postId)
    }
}

