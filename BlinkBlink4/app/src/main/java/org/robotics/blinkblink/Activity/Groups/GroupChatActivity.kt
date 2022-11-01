package org.robotics.blinkblink.Activity.Groups

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_chat.*
import org.robotics.blinkblink.Activity.Posts.PostsActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.*
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*

class GroupChatActivity:AppCompatActivity(),GroupChatAdapter.Listener {
    private lateinit var mListener: Utils.ValueEventListenerAdapter
    private lateinit var mRecivingUser: Users
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: GroupChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chat)
        closechat.setOnClickListener{
            finish()
        }
        coordinateBtnandInput2(sendmsg,chattext)



    }


    override fun onResume() {
        super.onResume()
        mSwipeRefreshLayout = refresh_chat

        initRecycleView()
        mListener = Utils.ValueEventListenerAdapter {
            mRecivingUser = it.getUserModel()
            initinfo()
        }
        val contact  = intent.getStringExtra("group")
        mRefUser = database.child(NODE_GROUPS).child(contact!!)
        mRefUser.addValueEventListener(mListener)
        sendmsg.setOnClickListener{
            mSmoothScrollToPosition = true
            val msg = chattext.text.toString()
            sendMessageToGroup(msg,contact!!, org.robotics.blinkblink.commons.TYPE_TEXT){

                chattext.setText("")
            }
        }

    }



    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }
    private fun initRecycleView() {
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView = messages_recycle_adapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = GroupChatAdapter(this)
        val contact = intent.getStringExtra("group")
        mRefMessages = database.child(NODE_GROUPS).child(contact!!).child(NODE_MESSAGES)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()
            if(mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            }else {
                mAdapter.addItemToTop(message) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }

        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }


    private fun initinfo() {
        usernsmechat.text = mRecivingUser.username
        statechat.text = mRecivingUser.state
        avatarchat.loadUserPhoto(mRecivingUser.photo)

        group_state.visibility = View.VISIBLE
        statechat.visibility = View.GONE

    }

    fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {

        var refMessages = "groups/$groupID/messages"
        val messageKey = database.child(refMessages).push().key

        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_FROM] =
            currentUid()!!
        mapMessage[CHILD_TYPE] = typeText
        mapMessage[CHILD_TEXT] = message
        mapMessage["read"] = false
        mapMessage[CHILD_ID] = messageKey.toString()
        mapMessage[CHILD_TIMESTAMP] =
            ServerValue.TIMESTAMP

        database.child(refMessages).child(messageKey.toString())
            .updateChildren(mapMessage)
            .addOnSuccessListener { function() }
            .addOnFailureListener { Toast.makeText(this,it.message.toString() , Toast.LENGTH_SHORT).show() }

    }
    override fun onPause() {
        super.onPause()
        mRefUser.removeEventListener(mListener)
        mRefMessages.removeEventListener(mMessagesListener)

    }

    //Функции лисенер рекуклер вью

    override fun readMsg(id: String, uid: String) {
        val contact = intent.getStringExtra("group")
        if(uid != currentUid()) {
            val reference =
                database.child(NODE_GROUPS).child(contact!!).child(NODE_MESSAGES).child(uid).child(CHILD_READ)
            reference.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                reference.setValue(true)
            })
        }


    }
    override fun openPosts(uid: String, postId: String) {
        val intent = Intent(this, PostsActivity ::class.java)
        intent.putExtra("uid",uid)
        intent.putExtra("id", postId)


        startActivities(arrayOf(intent))
    }

}