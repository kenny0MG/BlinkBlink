package org.robotics.blinkblink.Activity.SendPosts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_send_post.*
import org.robotics.blinkblink.Activity.Chats.HomeAdapter
import org.robotics.blinkblink.Activity.Chats.SwipeGesture
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.*
import org.robotics.blinkblink.models.CommonModel
import org.robotics.blinkblink.utils.*

private lateinit var mRecyclerView: RecyclerView
private lateinit var mAdapter: BottomSendAdapter
private val mRefMainList = database.child(NODE_MAIN_LIST).child(currentUid()!!)
private val mRefUsers = database.child(NODE_USERS)
private val mRefMessages = database.child(NODE_MESSAGES).child(currentUid()!!)
private var mListItems = listOf<CommonModel>()

class SendPostActivity : AppCompatActivity(), BottomSendAdapter.Listener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_post)
        initRecyclerView()
    }

    private fun initRecyclerView() {


        mRecyclerView = send_recycler
        mAdapter = BottomSendAdapter(this)

        // 1 запрос
        mRefMainList.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->

                when(model.type){
                    "chat" -> showChat(model)
                    "groups" -> showGroup(model)
                }

            }
        })

        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

    }


    ////второй запрос группы
    private fun showGroup(model: CommonModel) {
        // 2 запрос
        database.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot1 ->
                val newModel = dataSnapshot1.getUserModel()


                // 3 запрос
                database.child(NODE_GROUPS).child(model.id).child("messages")
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                        if (tempList.isEmpty()){
                            newModel.lastmsg = "Чат очищен"
                        } else {
                            newModel.lastmsg = tempList[0].text
                        }
                        if(tempList.isNotEmpty()){//вывод картирнки нового сообщения
                            newModel.from = tempList[0].from
                            if(newModel.from == currentUid()!!){
                                newModel.read = true
                            }else{
                                newModel.read = tempList[0].read
                            }

                        }
                        newModel.type = "groups"
                        mAdapter.updateListItems(newModel)
                    })
            })

    }



    /////третий запрос одиночные чаты
    private fun showChat(model: CommonModel) {
        // 2 запрос
        mRefUsers.child(model.id)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot1 ->
                val newModel = dataSnapshot1.getUserModel()

                // 3 запрос
                mRefMessages.child(model.id).limitToLast(1)
                    .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                        // в данной части кода мы выводим информцию на активити хоум
                        // (время последнего сообщения текст последнего сообщения и если сообщения не прочитаны то выводи картинку)
                        if (tempList.isEmpty()){

                            newModel.lastmsg = "Чат очищен"
                        } else {
                            newModel.lastmsg = tempList[0].text
                        }


                        if (tempList.isEmpty()){
                            newModel.read = true
                            newModel.lastmsg = "Чат очищен"
                        } else {
                            newModel.timeStampUser = tempList[0].timeStamp.toString().asTime()
                            if(tempList[0].type == "posts"){
                                newModel.lastmsg = "Отправлена запись"
                            }
                            else newModel.lastmsg = tempList[0].text
                        }





                        newModel.type = "chat"
                        mAdapter.updateListItems(newModel)
                    })
            })
    }
    //Отправка данных в чат
    override fun sendchat(uid: String) {
        val postId = intent.getStringExtra("postId")
        val userId = intent.getStringExtra("userId")
        val username = intent.getStringExtra("username")
        val postImage = intent.getStringExtra("postImage")
        val userImage = intent.getStringExtra("userImage")
        sendPost(uid,postId.toString(),userId.toString(),username.toString(),postImage.toString(),userImage.toString())
    }
//Отправка данных в группу
    override fun sendgroup(uid: String) {
        val postId = intent.getStringExtra("postId")
        val userId = intent.getStringExtra("userId")
        val username = intent.getStringExtra("username")
        val postImage = intent.getStringExtra("postImage")
        val userImage = intent.getStringExtra("userImage")
        sendMessageToGroup(uid,postId.toString(),userId.toString(),username.toString(),postImage.toString(),userImage.toString())
    }


    //Содержания сообщения типа "Пост" для групп
    fun sendMessageToGroup(groupID: String,userId: String,IdPost:String,username:String,image: String,userPhoto:String) {

        var refMessages = "groups/$groupID/messages"
        val messageKey = database.child(refMessages).push().key

        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_FROM] =
            currentUid()!!
        mapMessage["type"] = "posts"
        mapMessage["postId"] = IdPost
        mapMessage["userId"] = userId
        mapMessage["author"] = username
        mapMessage["imagePosts"] = image
        mapMessage["userPhoto"] = userPhoto
        mapMessage["read"] = false
        mapMessage[CHILD_ID] = messageKey.toString()
        mapMessage[CHILD_TIMESTAMP] =
            ServerValue.TIMESTAMP

        database.child(refMessages).child(messageKey.toString())
            .updateChildren(mapMessage)
            .addOnFailureListener { Toast.makeText(this,it.message.toString() , Toast.LENGTH_SHORT).show() }

    }

    //Содержания сообщения типа "Пост" для чатов
    fun sendPost(recivingUserId:String,userId: String,IdPost:String,username:String,image: String,userPhoto:String){
        var refDialogUser = "messages/${currentUid()!!}/$recivingUserId"
        var refDialogRecivingUser = "messages/$recivingUserId/${currentUid()!!}"
        val messageKey = database.child(refDialogUser).push().key


        val mapMessage = hashMapOf<String, Any>()
        mapMessage["from"] = currentUid()!!
        mapMessage["type"] = "posts"
        mapMessage["postId"] = IdPost
        mapMessage["userId"] = userId
        mapMessage["author"] = username
        mapMessage["imagePosts"] = image
        mapMessage["userPhoto"] = userPhoto
        mapMessage["read"] = false
        mapMessage["id"] = messageKey.toString()
        mapMessage["timeStamp"] = ServerValue.TIMESTAMP


        val mapDialog = hashMapOf<String,Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogRecivingUser/$messageKey"] = mapMessage

        database
            .updateChildren(mapDialog)
            .addOnFailureListener { showToast(it.message.toString()) }
    }
}