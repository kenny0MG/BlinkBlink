package org.robotics.blinkblink.Activity.Posts

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.internal.util.NotificationLite.getValue
import kotlinx.android.synthetic.main.activity_confid.*
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.android.synthetic.main.feeditem.*
import kotlinx.android.synthetic.main.feeditem.view.*
import kotlinx.android.synthetic.main.user.*
import org.robotics.blinkblink.Activity.Comments.CommentsActivity
import org.robotics.blinkblink.Activity.Main.FeedPostFavorite
import org.robotics.blinkblink.Activity.Main.FeedPostLikes
import org.robotics.blinkblink.Activity.SendPosts.SendPostActivity
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.BottomMoreFragment
import org.robotics.blinkblink.OtherUserActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.*
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*

class PostsActivity : AppCompatActivity() {
    private lateinit var mFeedPosts: FeedPosts
    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()
    private var mFavoriteListeners: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        val uid = intent.getStringExtra("uid")
        val id = intent.getStringExtra("id")
        like_image_activity_post.setOnClickListener {
            loadlikes(id.toString())
            toogleLike(id.toString())
            vibratePhone()
        }
        more_activity_posts.setOnClickListener{
            vibratePhone()
            var bottomfrSheets = BottomMoreFragment(uid!!,id!!)
            val fragmentManager =this.supportFragmentManager
            fragmentManager?.let{bottomfrSheets.show(it,"")}
        }
//Переключение сердечк и избранного в режим нажатого и наоборот
        database.child(NODE_LIKES).child(id!!).child(currentUid()!!).get().addOnSuccessListener {
            if(it.exists()){
                like_image_activity_post.setImageResource(R.drawable.ic_favorite_foreground)
            }else
            {
                like_image_activity_post.setImageResource( R.drawable.ic_likes_foreground)
            }
        }
        database.child(NODE_FAVORITE).child(id!!).child(currentUid()!!).get().addOnSuccessListener {
            if(it.exists()){
                izbr_activity_post.setImageResource(R.drawable.ic_izbrnoborder_foreground)
            }else
            {
                izbr_activity_post.setImageResource( R.drawable.ic_izbr_foreground)
            }
        }
        //


        //Двойной клик
        val doubleClick = DoubleClick(object : DoubleClickListener {
            override fun onSingleClickEvent(view: View?) {
                // DO STUFF SINGLE CLICK
            }

            override fun onDoubleClickEvent(view: View?) {
                toogleLike(id.toString())
                loadlikes(id.toString())
                vibratePhone()

            }
        })
// Обработчики нажатий
        comment_activity_post.setOnClickListener {
            CommentsActivity.start(this,id.toString())
        }
        send_activity_post.setOnClickListener {

            openchats() }
        postimage_activity_post.setOnClickListener(doubleClick)
//
        //Выво текста и изображений
        database.child(NODE_FEEDPOSTS).child(uid.toString()).child(id.toString())
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                mFeedPosts = it.getValue(FeedPosts::class.java)!!


                //Комментарий к посту
                val usernameSpann = SpannableString(mFeedPosts.username)
                usernameSpann.setSpan(StyleSpan(Typeface.BOLD),
                    0,
                    usernameSpann.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                usernameSpann.setSpan(RelativeSizeSpan(1.06f),0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                usernameSpann.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        if(mFeedPosts.uid == currentUid()!!){
                            val intent = Intent(widget.context, UserActivity::class.java)
                            widget.context.startActivities(arrayOf(intent))
                        }else{
                            val intent = Intent(widget.context, OtherUserActivity ::class.java)
                            intent.putExtra("uid",mFeedPosts.uid)
                            widget.context.startActivities(arrayOf(intent))
                        }
                    }
                    override fun updateDrawState(ds: TextPaint) {
                    }

                }, 0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                //


                //biousers.setText(mUser.bio, TextView.BufferType.EDITABLE)
                avatar_activity_posts.loadUserPhoto(mFeedPosts.photo)
                postimage_activity_post.loadImage(mFeedPosts.image)
                name_activity_posts.text = mFeedPosts.username
                time_activity_posts.setCaptionText(mFeedPosts.fileStemp())
                name_posts_activity.text = mFeedPosts.username.toUpperCase()
                if (mFeedPosts.caption == "") {
                    caption_text_activity_post.visibility = View.GONE
                } else {
                    caption_text_activity_post.visibility = View.VISIBLE
                    caption_text_activity_post.text =
                        SpannableStringBuilder().append(usernameSpann).append(" ").append(mFeedPosts.caption)
                    caption_text_activity_post.movementMethod = LinkMovementMethod.getInstance()


                }

            })
        izbr_activity_post.setOnClickListener {
            tooglefavorite(id.toString(),mFeedPosts.image)
            loadfavorite(id.toString())
        }


        //Вывод колличества лайков поста
        database.child(NODE_LIKES).child(id.toString())
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

                val like = it.children.map { it.getValue(Boolean::class.java)!! }
                //likecountes_activity_post.text=like.size.toString()
                //likecountes_activity_post.setText(like.size.toString(), TextView.BufferType.EDITABLE)
                if (like.size == 0) {
                    likecountes_activity_post.visibility = View.GONE
                } else {
                    likecountes_activity_post.visibility = View.VISIBLE
                    val LikesCountText = resources.getQuantityString(
                        R.plurals.likes_count, like.size)
                    likecountes_activity_post.text=like.size.toString() + " " + LikesCountText


                }
               }
            )
    }
//

    //Функции для работы с лайками комментами и избранным
    fun toogleLike(postId: String) {

        val reference = database.child(NODE_LIKES).child(postId).child(currentUid()!!)
        reference.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
            if (it.exists()) {
                like_image_activity_post.setImageResource( R.drawable.ic_likes_foreground)
                reference.removeValue()
            } else {
                //vibratePhone()
                like_image_activity_post.setImageResource(R.drawable.ic_favorite_foreground)
                reference.setValue(true).addOnSuccessListener {

                    EventBus.publish(Event.CreateLike(postId, currentUid()!!))
                }
            }
        })
    }

    fun loadlikes(postId: String) {
        fun createListener() = database.child(NODE_LIKES).child(postId).addValueEventListener(
            Utils.ValueEventListenerAdapter {
                val userLikes = it.children.map { it.key }.toSet()
                val postLikes =
                    FeedPostLikes(userLikes.size,
                        userLikes.contains(currentUid()!!))

            })
        if (mLikesListeners[postId] == null) {
            mLikesListeners += (postId to createListener())
        }

    }
    fun openchats() {
        val uid = intent.getStringExtra("uid")
        val id = intent.getStringExtra("id")
        val postId = id.toString()
        val userId= uid.toString()
        val username = mFeedPosts.username
        val postImage= mFeedPosts.image
        val userImage = mFeedPosts.photo
        val intent = Intent(this, SendPostActivity::class.java)
        intent.putExtra("postId",postId)
        intent.putExtra("userId",userId)
        intent.putExtra("username",username)
        intent.putExtra("postImage",postImage)
        intent.putExtra("userImage",userImage)
        startActivities(arrayOf(intent))
    }


     fun tooglefavorite(postId: String,image:String) {
        val reference = database.child(NODE_FAVORITE).child(postId).child(currentUid()!!)
        val referenceImageFavorite = database.child(NODE_FAVORITE_IMAGE).child(currentUid()!!).child(postId)

        reference.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
            if (it.exists()) {
                referenceImageFavorite.removeValue()
                reference.removeValue()
                izbr_activity_post.setImageResource( R.drawable.ic_izbr_foreground)

            } else {
                referenceImageFavorite.setValue(image)
                izbr_activity_post.setImageResource(R.drawable.ic_izbrnoborder_foreground)
                reference.setValue(true)
            }
        })
    }


     fun loadfavorite(postId: String) {
        fun createListener() = database.child(NODE_FAVORITE).child(postId).addValueEventListener(
            Utils.ValueEventListenerAdapter {
                val userIzbr = it.children.map { it.key }.toSet()
                val postIzbr =
                    FeedPostFavorite(userIzbr.size,
                        userIzbr.contains(currentUid()!!))

            })
        if (mFavoriteListeners[postId] == null) {
            mFavoriteListeners += (postId to createListener())
        }
    }
    //
    //Вибрация
    fun vibratePhone() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(20)
        }
    }

}