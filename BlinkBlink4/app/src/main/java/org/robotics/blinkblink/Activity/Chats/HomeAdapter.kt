package org.robotics.blinkblink.Activity.Chats

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_friends_item.view.*
import kotlinx.android.synthetic.main.main_item.*
import kotlinx.android.synthetic.main.main_item.view.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.asTime
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.commons.showToast
import org.robotics.blinkblink.models.CommonModel
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*

class HomeAdapter(private val listener: Listener):RecyclerView.Adapter<HomeAdapter.HomeHolder>() {
    private val listItems = mutableListOf<Users>()


    interface Listener {

        fun openchat(uid: String,id:String)
        fun opengroup(uid: String,id:String)




    }
    fun deleteItem(id:String){

        database.child(NODE_MAIN_LIST).child(currentUid()!!).child(id).removeValue()
    }

    class HomeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.main_username
        val itemLastMessage: TextView = view.main_lastmsg
        val itemPhoto: CircleImageView = view.main_photo
        val online: ImageView = view.online
        val time: TextView = view.time_main
        val newMsg: ImageView = view.circle_new_message
        val typetext: TextView = view.group_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)

        return HomeHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.itemName.text = listItems[position].username
        holder.itemLastMessage.text = listItems[position].lastmsg
        holder.itemPhoto.loadUserPhoto(listItems[position].photo)
        holder.time.text = listItems[position].timeStampUser.toString()


        if(listItems[position].type == "groups"){
            holder.typetext.visibility = View.VISIBLE

        }else{
            holder.typetext.visibility = View.GONE
        }
            if (!listItems[position].read) {
                holder.newMsg.visibility = View.VISIBLE

            } else {
                holder.newMsg.visibility = View.GONE

            }


        holder.itemView.setOnClickListener{
            when(listItems[holder.adapterPosition].type){
                "chat" ->listener.openchat(listItems[holder.adapterPosition].uid,listItems[holder.adapterPosition].id)
                "groups" -> listener.opengroup(listItems[holder.adapterPosition].id,listItems[holder.adapterPosition].id)
            }

        }

        //listener.delete(listItems[holder.adapterPosition].uid)
        if(listItems[position].state == "в сети"){
            holder.online.visibility = View.VISIBLE
        }else{
            holder.online.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item:Users){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}