package org.robotics.blinkblink.Activity.SingleChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.rxjava3.internal.operators.single.SingleDoOnSuccess
import kotlinx.android.synthetic.main.activity_add_chat.view.*
import kotlinx.android.synthetic.main.block_message_item.view.*
import kotlinx.android.synthetic.main.main_item.view.*
import org.robotics.blinkblink.Activity.Chats.HomeAdapter
import org.robotics.blinkblink.Activity.Groups.GroupChatActivity
import org.robotics.blinkblink.Activity.Main.FeedAdapter
import org.robotics.blinkblink.Activity.Users.SquareImageView
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.DiffUtilCalback
import org.robotics.blinkblink.commons.asTime
import org.robotics.blinkblink.commons.loadImage
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.models.CommonModel
import org.robotics.blinkblink.utils.currentUid

class SingleChatAdapter(private val listener: AddChatActivity):RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>()
     {
    private var mListMessagesCache = mutableListOf<CommonModel>()
    private lateinit var mDiffResult: DiffUtil.DiffResult
    interface Listener {
        fun readMsg(id: String,uid: String)
        fun openPosts(uid:String,id:String,img:ImageView)
        fun notificstion(uid:String,text:String)

    }


    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blocUserMessage: CardView = view.bloc_user_message
        val chatUserMessage: TextView = view.chat_user_message
        //val chatUserMessageTime: TextView = view.chat_user_message_time

        val blocReceivedMessage: CardView = view.bloc_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        //val chatReceivedMeessageTime: TextView = view.chat_received_message_time


        val blocReceivedImageMessage:LinearLayout = view.bloc_received_image_message
        val blocUserImageMessage:LinearLayout = view.bloc_user_image_message
        val chatUserImage:ImageView = view.squareImageViewUser
        val chatUserImageAuthorPhoto:CircleImageView = view.bloc_user_image_author_message

        val chatReceivedImage:ImageView = view.squareImageReceivedUser
        //val chatUserImageMessageTime:TextView = view.chat_user_image_message_time
        val chatReceivedImageMessage:TextView = view.chat_received_image_message
        val chatUserImageMessage:TextView = view.chat_user_image_message
        val chatReceivedImageAuthorPhoto:CircleImageView = view.bloc_received_image_author_message
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.block_message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when(mListMessagesCache[position].type){
            "text" -> drawMessageText(holder,position)
            "posts" -> drawMessageImage(holder,position)
        }

        listener.readMsg(mListMessagesCache[position].from,mListMessagesCache[position].id)



    }

         private fun drawMessageImage(holder: SingleChatAdapter.SingleChatHolder, position: Int) {
             holder.blocUserMessage.visibility = View.GONE
             holder.blocReceivedMessage.visibility = View.GONE
             holder.chatUserImage.setOnClickListener {
                 listener.openPosts(mListMessagesCache[position].postId,mListMessagesCache[position].userId,holder.chatUserImage)
             }
             holder.chatReceivedImage.setOnClickListener {
                 listener.openPosts(mListMessagesCache[position].postId,mListMessagesCache[position].userId,holder.chatUserImage)
             }
             if (mListMessagesCache[position].from == currentUid()) {
                 holder.blocReceivedImageMessage.visibility = View.GONE
                 holder.blocUserImageMessage.visibility = View.VISIBLE
                 holder.chatUserImage.loadImage(mListMessagesCache[position].imagePosts)
                 holder.chatUserImageAuthorPhoto.loadUserPhoto(mListMessagesCache[position].userPhoto)
                 holder.chatUserImageMessage.text = mListMessagesCache[position].author
                 //holder.chatUserImageMessage.text = mListMessagesCache[position].author
                 //holder.chatUserImageMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
             } else {
                 holder.blocReceivedImageMessage.visibility = View.VISIBLE
                 holder.blocUserImageMessage.visibility = View.GONE
                 holder.chatReceivedImageAuthorPhoto.loadUserPhoto(mListMessagesCache[position].userPhoto)
                 holder.chatReceivedImageMessage.text = mListMessagesCache[position].author
                 holder.chatReceivedImage.loadImage(mListMessagesCache[position].imagePosts)
                 // holder.chatReceivedImageMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
             }
         }

         private fun drawMessageText(holder: SingleChatAdapter.SingleChatHolder, position: Int) {
             holder.blocReceivedImageMessage.visibility = View.GONE
             holder.blocUserImageMessage.visibility = View.GONE
             if (mListMessagesCache[position].from == currentUid()) {
                 holder.blocUserMessage.visibility = View.VISIBLE
                 holder.blocReceivedMessage.visibility = View.GONE
                 holder.chatUserMessage.text = mListMessagesCache[position].text

                 //holder.chatUserMessageTime.text =
                     //mListMessagesCache[position].timeStamp.toString().asTime()

             } else {
                 holder.blocUserMessage.visibility = View.GONE
                 holder.blocReceivedMessage.visibility = View.VISIBLE
                 holder.chatReceivedMessage.text = mListMessagesCache[position].text
                 listener.notificstion(mListMessagesCache[position].from,mListMessagesCache[position].text)
                 //holder.chatReceivedMeessageTime.text =
                     //mListMessagesCache[position].timeStamp.toString().asTime()

             }
         }

         fun addItemToTop(item: CommonModel,
                     onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
    fun addItemToBottom(item: CommonModel,
                        onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }



}