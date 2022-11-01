package org.robotics.blinkblink

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_feed_post.*
import org.robotics.blinkblink.Activity.Comments.CommentsActivity
import org.robotics.blinkblink.Activity.Main.*
import org.robotics.blinkblink.Activity.SendPosts.SendPostActivity
import org.robotics.blinkblink.commons.Event
import org.robotics.blinkblink.commons.EventBus
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*
private lateinit var adapter: PostListAdapter
class FeedPostFragment : Fragment(),PostListAdapter.Listener {
    private val TAG = "ChatActivity"
    //private lateinit var mAdapter: FeedAdapter
    private lateinit var mUser: Users


    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()
    private var mFavoriteListeners: Map<String, ValueEventListener> = emptyMap()


    private val progressBar: ProgressBar by lazy {
        ProgressBar(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {




        adapter =
            PostListAdapter(this)
        database.child(NODE_FEEDPOSTS).child(currentUid()!!)
            .addValueEventListener(Utils.ValueEventListenerAdapter {
                val posts = it.children.map {
                    it.getValue(FeedPosts::class.java)?.copy(id = it.key.toString())!!
                }.sortedByDescending { it.fileStemp() }
                adapter.updateDataSet(posts)


                //adapter = PostImageAdapter(requireContext(),)
                feedrecycler.setHasFixedSize(true)
                feedrecycler.isNestedScrollingEnabled = false
                feedrecycler.adapter = adapter
                feedrecycler.layoutManager = LinearLayoutManager(getContext())

                Log.d(TAG, "feedPosts: ${posts.joinToString("\n", "\n")}")




            })
        return inflater.inflate(R.layout.fragment_feed_post, container, false)
    }



    companion object {

        @JvmStatic
        fun newInstance() = FeedPostFragment()
    }
    fun vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
                adapter.updatePostLikes(position, postLikes)
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
                adapter.updatePostIzbr(position, postIzbr)
            })
        if (mFavoriteListeners[postId] == null) {
            mFavoriteListeners += (postId to createListener())
        }
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


    fun openComments(postId: String) {
        CommentsActivity.start(requireContext(),postId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        adapter = ImageViewPagerAdapter(this)
//        ViewPager = findViewById(R.id.postimage)
//        ViewPager.adapter = adapter
    }

//    private fun loadPost(
//        onSuccess: (MutableList<FeedPosts>) -> Unit
//    ) {
//        progressBar.show()
//        val postList = mutableListOf<FeedPosts>()
//
//        Firebase.firestore.collection("post")
//            .get()
//            .addOnFailureListener {
//                progressBar.dismiss()
//                Snackbar.make(
//                    binding.root,
//                    "Нет подключения к интернету",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
//            .addOnCanceledListener {
//                progressBar.dismiss()
//            }
//            .addOnSuccessListener {
//                it.documents.forEach { ds ->
//                    val tmp = ds.toObject(FeedPosts::class.java)
//
//                    if (tmp != null)
//                        postList.add(tmp)
//                }
//
//                progressBar.dismiss()
//                onSuccess(postList)
//            }
//    }
}


