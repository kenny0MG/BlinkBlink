package org.robotics.blinkblink.Activity.SendPosts

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_item.view.*
import kotlinx.android.synthetic.main.main_item.view.group_text
import kotlinx.android.synthetic.main.main_item.view.main_lastmsg
import kotlinx.android.synthetic.main.main_item.view.main_photo
import kotlinx.android.synthetic.main.main_item.view.main_username
import kotlinx.android.synthetic.main.main_item.view.online
import kotlinx.android.synthetic.main.send_post_item.view.*
import org.robotics.blinkblink.Activity.Chats.HomeAdapter
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.models.Users

class BottomSendAdapter(private val listener:Listener): RecyclerView.Adapter<BottomSendAdapter.SendHolder>() {

    private val listItems = mutableListOf<Users>()
    interface Listener {

        fun sendchat(uid: String)
        fun sendgroup(uid: String)
//        fun opengroup(uid: String,id:String)
//        fun delete(uid:String)



    }
    class SendHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.main_username
        val itemLastMessage: TextView = view.main_lastmsg
        val itemPhoto: CircleImageView = view.main_photo
        val online: ImageView = view.online
        val send: AppCompatButton=view.button_send_posts
        val typetext: TextView = view.group_text
        val buttonOff: AppCompatButton = view.button_send_posts_off
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSendAdapter.SendHolder{
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.send_post_item, parent, false)

        return BottomSendAdapter.SendHolder(view)
    }


    override fun getItemCount(): Int  = listItems.size

    override fun onBindViewHolder(holder: SendHolder, position: Int) {
        holder.itemName.text = listItems[position].username
        holder.itemLastMessage.text = listItems[position].lastmsg
        holder.itemPhoto.loadUserPhoto(listItems[position].photo)
        //listener.delete(listItems[position].uid)

        if(listItems[position].type == "groups"){
            holder.typetext.visibility = View.VISIBLE

        }else{
            holder.typetext.visibility = View.GONE
        }

        if(listItems[position].state == "в сети"){
            holder.online.visibility = View.VISIBLE
        }else{
            holder.online.visibility = View.GONE

        }
        holder.send.setOnClickListener{
            holder.send.visibility=View.GONE
            holder.buttonOff.visibility=View.VISIBLE
            when(listItems[holder.adapterPosition].type){
                "chat" ->listener.sendchat(listItems[holder.adapterPosition].uid)
                "groups" -> listener.sendgroup(listItems[holder.adapterPosition].id)

            }

        }


    }
    fun updateListItems(item:Users){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }


}