package org.robotics.blinkblink

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.robotics.blinkblink.models.Users
import kotlinx.android.synthetic.main.add_friends_item.view.*
import kotlinx.android.synthetic.main.feeditem.view.*
import org.robotics.blinkblink.commons.loadImage
import org.robotics.blinkblink.Activity.Main.SimpleCallback
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.utils.currentUid

//адаптер для добавления друзей
class FriendsAdapter(private val listener:Listener): RecyclerView.Adapter<FriendsAdapter.ViewHolder> (){
    private var mPositions = mapOf<String,Int>()!!
    private var mFollows = mapOf<String, Boolean>()!!
    private var mUsers =  listOf<Users>()!!
    interface Listener{
        fun follow(uid:String)
        fun unfollow(uid:String)
        fun click(uid:String)
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_friends_item,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            val user = mUsers[position]
            view.imageuersall.loadUserPhoto(mUsers[position].photo)
            view.usersnameall.text = mUsers[position].username
            view.usersusernaeall.text = mUsers[position].name
            val follows = mFollows[mUsers[position].uid] ?: false
            view.buttonred.setOnClickListener{listener.follow(user.uid)}
            view.buttongrey.setOnClickListener{listener.unfollow(user.uid)}
            if(follows){
                view.buttonred.visibility = View.GONE
                view.buttongrey.visibility = View.VISIBLE
            }
            else{
                view.buttonred.visibility = View.VISIBLE
                view.buttongrey.visibility = View.GONE
            }
            if(!user.proverka){
                view.proverkaadd.visibility = View.GONE
            }else{
                view.proverkaadd.visibility = View.VISIBLE
            }
            itemView.setOnClickListener{
               listener.click(user.uid)
            }
        }
    }

    override fun getItemCount(): Int = mUsers.size

    fun update(users: List<Users>,follows:Map<String,Boolean>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(mUsers, users) { it.uid })
        mUsers = users
        mPositions = users.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows
        diffResult.dispatchUpdatesTo(this)
    }

    fun followed(uid: String) {
        mFollows+=(uid to true)
        notifyItemChanged(mPositions[uid]!!)

    }

    fun unfollowed(uid: String) {
        mFollows += (uid to false)
        notifyItemChanged(mPositions[uid]!!)
    }

}