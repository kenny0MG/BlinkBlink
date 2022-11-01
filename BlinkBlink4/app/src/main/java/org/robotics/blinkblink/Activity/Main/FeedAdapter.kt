package org.robotics.blinkblink.Activity.Main

import android.annotation.SuppressLint
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
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import kotlinx.android.synthetic.main.feeditem.view.*
import org.robotics.blinkblink.Activity.ImageListPerson.ImagesActivity
import org.robotics.blinkblink.OtherUserActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.FeedPostFragment
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.commons.setCaptionText
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.fromUrl


data class FeedPostFavorite(val favoritecount: Int, val favorite: Boolean)
data class FeedPostLikes(val likescount: Int, val likes: Boolean)



class FeedAdapter(private val listener: Listener) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {


    interface Listener {
        fun toogleLike(postId: String)
        fun loadlikes(postId: String, position: Int)
        fun openComm(postId: String)
        fun tooglefavorite(postId: String,image:String)
        fun loadfavorite(postId: String, position: Int)
        fun openbottom(postId: String,uid:String)
        fun openchats(postId: String,userId: String,username:String,postImage:String,userImage:String)


    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    private var posts = listOf<FeedPosts>()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private var postfavorite: Map<Int, FeedPostFavorite> = emptyMap()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feeditem, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetText")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//            val currentDate = sdf.format(Date())
        val post = posts[position]

        with(holder.view) {
            avatar_posts.loadUserPhoto(post.photo)
            //val options= RequestOptions()
            //val options =RequestOptions().placeholder(R.drawable.line)
            //Glide.with(this).asBitmap().load(post.image).apply(options).centerCrop().into()
            //postimage.loadImage(post.image)
            val options = RequestOptions()
            postimage.fromUrl(post.image)
            Glide.with(this).setDefaultRequestOptions(options).load(post.image).fitCenter().into(postimage);

            val likes = postLikes[position] ?: FeedPostLikes(
                0,
                false)
            val favorite = postfavorite[position] ?: FeedPostFavorite(
                0,
                false)
            name_posts.text = post.username
            time.setCaptionText(post.fileStemp())
            if (likes.likescount == 0) {
                likecountes.visibility = View.GONE
            } else {
                likecountes.visibility = View.VISIBLE
                val LikesCountText = context.resources.getQuantityString(
                    R.plurals.likes_count, likes.likescount)
                likecountes.text=likes.likescount.toString() + " " + LikesCountText


            }
//            if(post.proverka == false){
//                proverkacom.visibility = View.GONE
//            }else{
//                proverkacom.visibility = View.VISIBLE
//
//            }
            val doubleClick = DoubleClick(object : DoubleClickListener {
                override fun onSingleClickEvent(view: View?) {
                    // DO STUFF SINGLE CLICK
                }

                override fun onDoubleClickEvent(view: View?) {
                    listener.toogleLike(post.id)
                    like_image.setImageResource(if (likes.likes) R.drawable.ic_favorite_foreground else R.drawable.ic_likes_foreground)
                }
            })
            postimage.setOnClickListener(doubleClick)
            comments_value.setOnClickListener{listener.openComm(post.id)}
            comment.setOnClickListener{listener.openComm(post.id)}
            like_image.setOnClickListener { listener.toogleLike(post.id) }
            like_image.setImageResource(if (likes.likes) R.drawable.ic_favorite_foreground else R.drawable.ic_likes_foreground)
            listener.loadlikes(post.id, position)
            izbr_post.setOnClickListener { listener.tooglefavorite(post.id,post.image) }
            izbr_post.setImageResource(if (favorite.favorite) R.drawable.ic_izbrnoborder_foreground else R.drawable.ic_izbr_foreground)
            listener.loadfavorite(post.id, position)

            more.setOnClickListener{listener.openbottom(post.id,post.uid)}
            name_posts.setOnClickListener{
                if(posts[position].uid == currentUid()!!){
                    val intent = Intent(context, UserActivity::class.java)
                    context.startActivities(arrayOf(intent))
                }else{
                    val intent = Intent(context, OtherUserActivity ::class.java)
                    intent.putExtra("uid",posts[position].uid)


                    context.startActivities(arrayOf(intent))
                }
            }
            send_post.setOnClickListener{
               listener.openchats(post.id,post.uid,post.username,post.image, post.photo.toString())
            }
            avatar_posts.setOnClickListener{
                if(posts[position].uid == currentUid()!!){
                    val intent = Intent(context, UserActivity::class.java)
                    context.startActivities(arrayOf(intent))
                }else{
                    val intent = Intent(context, OtherUserActivity ::class.java)
                    intent.putExtra("uid",posts[position].uid)


                    context.startActivities(arrayOf(intent))
                }
            }




        }


        val usernameSpann = SpannableString(post.username)
        usernameSpann.setSpan(StyleSpan(Typeface.BOLD),
            0,
            usernameSpann.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpann.setSpan(RelativeSizeSpan(1.06f),0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpann.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if(posts[position].uid == currentUid()!!){
                    val intent = Intent(widget.context, UserActivity::class.java)
                    widget.context.startActivities(arrayOf(intent))
                }else{
                    val intent = Intent(widget.context, OtherUserActivity ::class.java)
                    intent.putExtra("uid",posts[position].uid)
                    widget.context.startActivities(arrayOf(intent))
                }


            }

            override fun updateDrawState(ds: TextPaint) {

            }

        }, 0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (post.caption == "") {
            holder.view.caption_text.visibility = View.GONE
        } else {
            holder.view.caption_text.visibility = View.VISIBLE
            holder.view.caption_text.text =
                SpannableStringBuilder().append(usernameSpann).append(" ").append(post.caption)
            holder.view.caption_text.movementMethod = LinkMovementMethod.getInstance()


        }

    }


    override fun getItemCount()=posts.size


    private fun ImageView.loadImage(image: String?) {
        val options =RequestOptions()
        Glide.with(this).asBitmap().load(image).apply(options).centerCrop().into(this)
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }
    fun updatePostIzbr(position: Int, izbr: FeedPostFavorite) {
        postfavorite += (position to izbr)
        notifyItemChanged(position)
    }

    fun updatePosts(posts: List<FeedPosts> ) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts,posts){it.id})
        this.posts = posts
        diffResult.dispatchUpdatesTo(this)
    }
}




class SimpleCallback<T>(private val oldItems: List<T>,private val newItems: List<T>,private val itemIdGetter:(T) -> Any): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = itemIdGetter(oldItems[oldItemPosition]) == itemIdGetter(newItems[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition] == newItems[newItemPosition]

}


