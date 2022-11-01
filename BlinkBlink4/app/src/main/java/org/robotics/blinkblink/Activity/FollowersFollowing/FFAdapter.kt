package org.robotics.blinkblink.Activity.FollowersFollowing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacns_item.view.*
import kotlinx.android.synthetic.main.newchat_item.view.*
import org.robotics.blinkblink.Activity.Groups.ChatAdapter
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.models.Users

class FFAdapter() : RecyclerView.Adapter<FFAdapter.FollowingFollowersHolder>(){

    private var listItems = mutableListOf<Users>()


    class FollowingFollowersHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.usernamenewhat
        val state: TextView = view.namenewchat
        val itemPhoto: CircleImageView = view.avatarnewchat

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FollowingFollowersHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.newchat_item, parent, false)
        return FollowingFollowersHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingFollowersHolder, position: Int) {
        holder.itemName.text = listItems[position].username
        holder.state.text = listItems[position].lastmsg
        holder.itemPhoto.loadUserPhoto(listItems[position].photo)
    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item: Users){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}