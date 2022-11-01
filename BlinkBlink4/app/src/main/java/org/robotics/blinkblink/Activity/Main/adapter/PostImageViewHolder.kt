package org.robotics.blinkblink

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat.startActivities
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import kotlinx.android.synthetic.main.feeditem.view.*
import org.robotics.blinkblink.Activity.Main.FeedPostFavorite
import org.robotics.blinkblink.Activity.Main.FeedPostLikes
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.commons.setCaptionText
import org.robotics.blinkblink.databinding.PostImageItemBinding
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.fromUrl


class PostImageViewHolder(private val binding: PostImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        postItem: FeedPosts,
        postLike: FeedPostLikes?,
        postFavorite: FeedPostFavorite?,
        listener: PostListAdapter.Listener,

    ) {

        binding.postimage.fromUrl(postItem.image)
        binding.avatarPosts.loadUserPhoto(postItem.photo)
        binding.namePosts.text = postItem.username
        binding.time.setCaptionText(postItem.fileStemp())
        val usernameSpann = SpannableString(postItem.username)
        usernameSpann.setSpan(StyleSpan(Typeface.BOLD),
            0,
            usernameSpann.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpann.setSpan(RelativeSizeSpan(1.06f),0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpann.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if(postItem.uid == currentUid()!!){
                    val intent = Intent(widget.context, UserActivity::class.java)
                    widget.context.startActivities(arrayOf(intent))
                }else{
                    val intent = Intent(widget.context, OtherUserActivity ::class.java)
                    intent.putExtra("uid",postItem.uid)
                    widget.context.startActivities(arrayOf(intent))
                }


            }

            override fun updateDrawState(ds: TextPaint) {

            }

        }, 0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)



        if (postItem.caption == "") {
            binding.captionText.visibility = View.GONE
        } else {
            binding.captionText.visibility = View.VISIBLE
            binding.captionText.text =
                SpannableStringBuilder().append(usernameSpann).append(" ").append(postItem.caption)
            binding.captionText.movementMethod = LinkMovementMethod.getInstance()


        }



        val likes = postLike ?: FeedPostLikes(
            0,
            false)
//        val favorite = postfavorite[position] ?: FeedPostFavorite(
//            0,
//            false)
        val favorite = postFavorite ?: FeedPostFavorite(
            0,
            false)



        val doubleClick = DoubleClick(object : DoubleClickListener {
            override fun onSingleClickEvent(view: View?) {
                // DO STUFF SINGLE CLICK
            }

            override fun onDoubleClickEvent(view: View?) {
                listener.toogleLike(postItem.id)
                binding.likeImage.setImageResource(if (likes.likes) R.drawable.ic_favorite_foreground else R.drawable.ic_likes_foreground)
            }
        })




        if (likes.likescount == 0) {
            binding.likecountes.visibility = View.GONE
        } else {
            binding.likecountes.visibility = View.VISIBLE
//            val LikesCountText = .res.quantityFromRes(
//                R.plurals.likes_count, likes.likescount)
            binding.likecountes.text=likes.likescount.toString() + " " + "likes"


        }


        binding.likeImage.setOnClickListener { listener.toogleLike(postItem.id) }
        binding.likeImage.setImageResource(if (likes.likes) R.drawable.ic_favorite_foreground else R.drawable.ic_likes_foreground)
        listener.loadlikes(postItem.id, position)
        binding.izbrPost.setOnClickListener { listener.tooglefavorite(postItem.id,postItem.image) }
        binding.izbrPost.setImageResource(if (favorite.favorite) R.drawable.ic_izbrnoborder_foreground else R.drawable.ic_izbr_foreground)
        listener.loadfavorite(postItem.id, position)
        binding.commentsValue.setOnClickListener{listener.openComm(postItem.id)}
        binding.comment.setOnClickListener{listener.openComm(postItem.id)}
        binding.more.setOnClickListener{listener.openbottom(postItem.id,postItem.uid)}
        binding.postimage.setOnClickListener(doubleClick)

//        binding.namePosts.setOnClickListener{
//            if(postItem.uid == currentUid()!!){
//                val intent = Intent(context, UserActivity::class.java)
//                startActivities(arrayOf(intent))
//            }else{
//                val intent = Intent(context, OtherUserActivity ::class.java)
//                intent.putExtra("uid",posts[position].uid)
//
//
//                context.startActivities(arrayOf(intent))
//            }
//        }
        binding.sendPost.setOnClickListener{
            listener.openchats(postItem.id,postItem.uid,postItem.username,postItem.image, postItem.photo.toString())
        }
//        avatar_posts.setOnClickListener{
//            if(posts[position].uid == currentUid()!!){
//                val intent = Intent(context, UserActivity::class.java)
//                context.startActivities(arrayOf(intent))
//            }else{
//                val intent = Intent(context, OtherUserActivity ::class.java)
//                intent.putExtra("uid",posts[position].uid)
//
//
//                context.startActivities(arrayOf(intent))
//            }
//        }

        // binding.captionText.isVisible = postItem.caption.isNotEmpty()
    }

//    fun Context.quantityFromRes(id_: Int, qtt:Int) = resources.getQuantityString(id_, qtt)

}