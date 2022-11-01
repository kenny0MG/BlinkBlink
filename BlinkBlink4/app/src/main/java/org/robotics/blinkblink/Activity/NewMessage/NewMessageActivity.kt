package org.robotics.blinkblink.Activity.NewMessage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.newchat_item.view.*
import org.robotics.blinkblink.*
import org.robotics.blinkblink.Activity.ConfidActivity
import org.robotics.blinkblink.Activity.Groups.AddContactsActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.Activity.SingleChat.AddChatActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.utils.NODE_FOLLOW
import org.robotics.blinkblink.utils.NODE_USERS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database


class NewMessageActivity : baseActivity() {

    private lateinit var mAdapter: FirebaseRecyclerAdapter<Users, NewMessageHolder>

    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)




        //mRecyclerView = new_message_recycle
        initRecycleView()
        closenewmsg.setOnClickListener{
            finish()
        }
        checkNewMessageProgressBar()
        makegroups.setOnClickListener{
            val intent = Intent(this, AddContactsActivity::class.java)
            startActivity(intent)
        }


}

    private fun initRecycleView() {
        mRefContacts = database.child(NODE_FOLLOW).child(currentUid()!!)

        val options = FirebaseRecyclerOptions.Builder<Users>()
            .setQuery(mRefContacts, Users::class.java)
            .build()

        mAdapter = object : FirebaseRecyclerAdapter<Users, NewMessageHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.newchat_item, parent, false)
                return NewMessageHolder(view)
            }

            override fun onBindViewHolder(
                holder: NewMessageHolder,
                position: Int,
                model: Users
            ) {

                mRefUsers= database.child(NODE_USERS).child(model.uid)
                mRefUsers.addValueEventListener(Utils.ValueEventListenerAdapter{
                    val contact = it.getValue(Users::class.java)
                    holder.username.text = contact!!.username
                    holder.status.text = contact.state
                    holder.photo.loadUserPhoto(contact.photo)
                    holder.itemView.setOnClickListener {
                        val intent = Intent(applicationContext, AddChatActivity::class.java)
                        intent.putExtra("uid",contact.uid)
                        startActivity(intent)

                    }
                })





            }
        }
        new_message_recycle.layoutManager = LinearLayoutManager(this)

        new_message_recycle.adapter = mAdapter
        mAdapter.startListening()

    }


    private fun checkNewMessageProgressBar() {


        database.child(NODE_FOLLOW).child(currentUid()!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (progress_new_message != null) {
                        progress_new_message.visibility = View.GONE

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "$error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

class NewMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
    val username: TextView = view.usernamenewhat
    val status: TextView = view.namenewchat
    val photo: CircleImageView = view.avatarnewchat
    //val itemview : AppCompatButton = view.buttonnewchat
}
