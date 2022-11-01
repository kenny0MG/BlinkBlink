package org.robotics.blinkblink.Activity.Main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_recommendation.*
import org.robotics.blinkblink.Activity.Comments.CommentsActivity
import org.robotics.blinkblink.Activity.SendPosts.SendPostActivity
import org.robotics.blinkblink.BottomMoreFragment
import org.robotics.blinkblink.PostListAdapter

import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.Event
import org.robotics.blinkblink.commons.EventBus
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*


class RecommendationFragment : Fragment(), PostListAdapter.Listener {
    private val TAG = "ChatActivity"
    private lateinit var adapter: PostListAdapter
    private lateinit var mUser: Users
    private var mFavoriteListeners: Map<String, ValueEventListener> = emptyMap()


    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        adapter =
            PostListAdapter(this)
        database.child(NODE_RECOMMENDATIONPOSTS)
            .addValueEventListener(Utils.ValueEventListenerAdapter {
                val posts = it.children.map {
                    it.getValue(FeedPosts::class.java)?.copy(id = it.key.toString())!!
                }.sortedByDescending { it.fileStemp() }

                feedrecyclerrek.adapter = adapter
                feedrecyclerrek.layoutManager = LinearLayoutManager(getContext())
                adapter.updateDataSet(posts)

                Log.d(TAG, "feedPosts: ${posts.joinToString("\n", "\n")}")



            })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendation, container, false)
    }
    fun vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(20)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = RecommendationFragment()
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
            adapter.updatePostLikes(position, postLikes)
        })
    if (mLikesListeners[postId] == null) {
        mLikesListeners += (postId to createListener())
    }

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
                adapter.updatePostIzbr(position, postIzbr)
            })
        if (mFavoriteListeners[postId] == null) {
            mFavoriteListeners += (postId to createListener())
        }
    }

    override fun openComm(postId: String) {
        openComments(postId)
    }

    fun openComments(postId: String) {
       CommentsActivity.start(requireContext(),postId)
    }
    override fun openbottom(postId: String,uid:String) {
        vibratePhone()
        var bottomfrSheets = BottomMoreFragment(uid,postId)
        val fragmentManager =(activity as FragmentActivity).supportFragmentManager
        fragmentManager?.let{bottomfrSheets.show(it,"")}
    }

    override fun openchats(postId: String,userId: String,username:String,postImage:String,userImage:String) {
        vibratePhone()
        val intent = Intent(context, SendPostActivity::class.java)
        intent.putExtra("postId",postId)
        intent.putExtra("userId",userId)
        intent.putExtra("username",username)
        intent.putExtra("postImage",postImage)
        intent.putExtra("userImage",userImage)
        requireContext().startActivities(arrayOf(intent))
    }

}

