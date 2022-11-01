package org.robotics.blinkblink.Activity.Comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.feed_comments.view.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.commons.setCaptionText
import org.robotics.blinkblink.Activity.Main.SimpleCallback
import org.robotics.blinkblink.FriendsAdapter
import org.robotics.blinkblink.models.Comment

class CommentsAdapter(private val listener: Listener): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener{

        fun click(uid:String)
    }
    private var comments = listOf<Comment>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_comments, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]

        with(holder.view) {
            avatarcom.loadUserPhoto(comment.photo)
            commenttext.text = (comment.text)
            commetusername.text =(comment.username)
            timestemp.setCaptionText(comment.fileStemp())
            if(comment.proverka == false){
                proverkacom.visibility = View.GONE
            }else{
                proverkacom.visibility = View.VISIBLE

            }

        }
        holder.view.avatarcom.setOnClickListener{
            listener.click(comments[position].uid)
        }
        holder.view.commetusername.setOnClickListener{
            listener.click(comments[position].uid)
        }

    }
    fun updateComments(newComments: List<Comment>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.comments, newComments) {it.id})
        this.comments = newComments
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = comments.size

}

