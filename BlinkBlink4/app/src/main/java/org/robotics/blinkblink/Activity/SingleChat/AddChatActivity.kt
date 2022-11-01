package org.robotics.blinkblink.Activity.SingleChat

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.*
import edmt.dev.videoplayer.utils.VerticalSpacingItemDecorator
import kotlinx.android.synthetic.main.activity_add_chat.*
import org.robotics.blinkblink.Activity.ImageListPerson.ImagesActivity
import org.robotics.blinkblink.Activity.Posts.PostsActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.*
import org.robotics.blinkblink.commons.TYPE_TEXT
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*


private lateinit var mUser: Users
class AddChatActivity() :SingleChatAdapter.Listener,baseActivity(){
    private lateinit var mListener: Utils.ValueEventListenerAdapter
    private lateinit var mRecivingUser: Users
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener:AppChildEventListener
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chat)


        coordinateBtnandInput2(sendmsg,chattext)


        close_chat.setOnClickListener {
            finish()
        }
        mSwipeRefreshLayout = refresh_chat
        initRecycleView()


        mListener = Utils.ValueEventListenerAdapter {
            mRecivingUser = it.getUserModel()
            initinfo()

        }
        val contact  = intent.getStringExtra("uid")
        mRefUser = database.child(NODE_USERS).child(contact!!)
        mRefUser.addValueEventListener(mListener)
        sendmsg.setOnClickListener{
            mSmoothScrollToPosition = true
            val msg = chattext.text.toString()
            if(chattext.text.isNotEmpty()){
                sendmessage(msg,contact!!, TYPE_TEXT){
                    saveToMainList(contact,"chat")
                    chattext.setText("")
                }
            }

        }


    }



    override fun onResume() {
        super.onResume()


    }

     fun saveToMainList(id: String, type: String) {
         val refUsers = "$NODE_MAIN_LIST/${currentUid()}/$id"
         val refReceived = "$NODE_MAIN_LIST/$id/${currentUid()}"

         val mapUser = hashMapOf<String,Any>()
         val mapReceived = hashMapOf<String,Any>()

         mapUser[CHILD_ID] = id
         mapUser[CHILD_TYPE] = type

         mapReceived[CHILD_ID] = currentUid()!!
         mapReceived[CHILD_TYPE] = type

         val commonMap = hashMapOf<String,Any>()
         commonMap[refUsers] = mapUser
         commonMap[refReceived] = mapReceived

         database.updateChildren(commonMap)
             .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }
    private fun initRecycleView() {
        mAdapter = SingleChatAdapter(this)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView = messages_recycle_adapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        val itemDecorator = VerticalSpacingItemDecorator(-8)
        mRecyclerView.addItemDecoration(itemDecorator)

        val contact = intent.getStringExtra("uid")

        mRefMessages = database.child(NODE_MESSAGES).child(currentUid()!!).child(contact!!)
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


            group_state.visibility = View.GONE
            statechat.visibility = View.VISIBLE


    }

    private fun sendmessage(msg: String, recivingUserId: String, typeText: String, function: () -> Unit) {
        var refDialogUser = "messages/${currentUid()!!}/$recivingUserId"
        var refDialogRecivingUser = "messages/$recivingUserId/${currentUid()!!}"
        val messageKey = database.child(refDialogUser).push().key


        val mapMessage = hashMapOf<String, Any>()
        mapMessage["from"] = currentUid()!!
        mapMessage["type"] = typeText
        mapMessage["text"] = msg
        mapMessage["read"] = false
        mapMessage["id"] = messageKey.toString()
        mapMessage["timeStamp"] = ServerValue.TIMESTAMP


        val mapDialog = hashMapOf<String,Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogRecivingUser/$messageKey"] = mapMessage

        database
            .updateChildren(mapDialog)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }
    override fun onPause() {
        super.onPause()
        mRefUser.removeEventListener(mListener)
        mRefMessages.removeEventListener(mMessagesListener)

    }

    override fun readMsg(uid: String,id: String) {//функция читки сообщений
       if(uid != currentUid()) {
           val reference =
               database.child(NODE_MESSAGES).child(currentUid()!!).child(uid).child(id).child("read")
           reference.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
               reference.setValue(true)
           })
       }
    }

    override fun openPosts(uid: String, id: String,img:ImageView) {
        val intent = Intent(this, PostsActivity ::class.java)
        intent.putExtra("uid",uid)
        intent.putExtra("id", id)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,img,
           "block_image_message")
        //, options.toBundle()
        startActivity(intent)



    }

    override fun notificstion(uid: String, text: String) {
        database.child(NODE_USERS).child(uid).addListenerForSingleValueEvent(
            Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!
                val builder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(mUser.username)
                    .setContentText(text)
                val notifications = builder.build()
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0,notifications)
            })


    }


}



