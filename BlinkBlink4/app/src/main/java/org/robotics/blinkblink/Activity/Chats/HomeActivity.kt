package org.robotics.blinkblink.Activity.Chats



import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed_post.*
import kotlinx.android.synthetic.main.main_item.*
import kotlinx.android.synthetic.main.user.*
import org.robotics.blinkblink.Activity.Groups.GroupChatActivity

import org.robotics.blinkblink.Activity.NewMessage.NewMessageActivity
import org.robotics.blinkblink.Activity.SingleChat.AddChatActivity
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.asTime
import org.robotics.blinkblink.commons.getCommonModel
import org.robotics.blinkblink.commons.getUserModel
import org.robotics.blinkblink.models.CommonModel
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*
import org.robotics.blinkblink.views.setupButtonNavigations

class HomeActivity : baseActivity() , HomeAdapter.Listener{
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: HomeAdapter
    private val mRefMainList = database.child(NODE_MAIN_LIST).child(currentUid()!!)
    private val mRefUsers = database.child(NODE_USERS)
    private val mRefMessages = database.child(NODE_MESSAGES).child(currentUid()!!)
    private var mListItems = listOf<CommonModel>()
    private lateinit var mUser: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupButtonNavigations(1)
        addperson.setOnClickListener{
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        database.child(NODE_USERS).child(currentUid()!!)//фото в верхнем левом углу чаты
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!

                Glide.with(this).load(mUser.photo).placeholder(R.drawable.person).into(home_photo)
            })

        home_photo.setOnClickListener{
            val intent = Intent(this, UserActivity::class.java)
            startActivities(arrayOf(intent))
        }



    }

    override fun onResume() {
        super.onResume()
        checkHomeChats()
    }




    private fun checkHomeChats() {

        database.child(NODE_MAIN_LIST).child(currentUid()!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (shimmer_home != null) {
                        initRecyclerView()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "$error", Toast.LENGTH_SHORT).show()
                }
            })
    }





    ////Первый запрос проверка типа чата
    private fun initRecyclerView() {

        shimmer_home.stopShimmer()
        shimmer_home.visibility = View.GONE
        main_home_text_progress.visibility = View.GONE
        main_home_text.visibility = View.VISIBLE
        mRecyclerView = recyclerHome
        mAdapter = HomeAdapter(this)

        // 1 запрос
        mRefMainList.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }.sortedByDescending { it.timeStamp.toString() }
            mListItems.forEach { model ->
                val swipegesture = object: SwipeGesture(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        when(direction){
                            ItemTouchHelper.LEFT->{
                                vibratePhone()
                                mAdapter.deleteItem(model.id)


                            }
//                            ItemTouchHelper.RIGHT ->{
//                            }
                        }
                    }
                }
                val touchHelper=ItemTouchHelper(swipegesture)
                touchHelper.attachToRecyclerView(recyclerHome)

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
    private fun showGroup(model:CommonModel ) {
        // 2 запрос
        database.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot1 ->
                val newModel = dataSnapshot1.getUserModel()
                // 3 запрос
                database.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                        if (tempList.isEmpty()){
                            newModel.read = true
                            newModel.lastmsg = "Чат очищен"
                        } else {
                            newModel.timeStampUser = tempList[0].timeStamp.toString().asTime()
                            if(tempList[0].type == "posts"){
                                newModel.lastmsg= "Отправлена запись"
                            }
                            else newModel.lastmsg = tempList[0].text
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
                            newModel.read = true
                            newModel.lastmsg = "Чат очищен"
                        } else {
                            newModel.timeStampUser = tempList[0].timeStamp.toString().asTime()
                            if(tempList[0].type == "posts"){
                                newModel.lastmsg = "Отправлена запись"
                            }
                            else newModel.lastmsg = tempList[0].text
                        }

                        if(tempList.isNotEmpty()){//вывод картирнки нового сообщения
                            newModel.from = tempList[0].from
                            if(newModel.from == currentUid()!!){
                                newModel.read = true
                            }else{
                                newModel.read = tempList[0].read


                            }

                        }


                        newModel.type = "chat"
                        mAdapter.updateListItems(newModel)
                    })
            })
    }



    //////Функции адаптера
    override fun openchat(uid: String,id:String) {//открытие чата
        val intent = Intent(applicationContext, AddChatActivity::class.java)
        intent.putExtra("uid",uid)
        intent.putExtra("id",id)
        startActivity(intent)
    }

    override fun opengroup(uid: String,id:String) {//открытие группы
        val intent = Intent(applicationContext, GroupChatActivity::class.java)
        intent.putExtra("group",uid)
        intent.putExtra("id",id)
        startActivity(intent)
    }

    fun vibratePhone() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(20)
        }
    }


}

