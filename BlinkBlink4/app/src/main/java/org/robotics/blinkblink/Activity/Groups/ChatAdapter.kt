package org.robotics.blinkblink.Activity.Groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacns_item.view.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.models.Users

class ChatAdapter():RecyclerView.Adapter<ChatAdapter.AddContactsHolder>() {
    private var listItems = mutableListOf<Users>()

    class AddContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.useradd
        val state: TextView = view.nameadd
        val itemPhoto: CircleImageView = view.avataradd
        val itemChoice: CircleImageView = view.add_contacts_item_choice


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_contacns_item, parent, false)
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener {
            if (listItems[holder.adapterPosition].choice){
                holder.itemChoice.visibility = View.INVISIBLE
                listItems[holder.adapterPosition].choice = false
                AddContactsActivity.listContacts.remove(listItems[holder.adapterPosition])
            } else {
                holder.itemChoice.visibility = View.VISIBLE
                listItems[holder.adapterPosition].choice = true
                AddContactsActivity.listContacts.add(listItems[holder.adapterPosition])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
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