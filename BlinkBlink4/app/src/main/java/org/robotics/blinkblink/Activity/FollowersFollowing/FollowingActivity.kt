package org.robotics.blinkblink.Activity.FollowersFollowing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_followers.*
import kotlinx.android.synthetic.main.activity_following.*
import org.robotics.blinkblink.Activity.Groups.ChatAdapter
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.getCommonModel
import org.robotics.blinkblink.commons.getUserModel
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*


private lateinit var mRecyclerView: RecyclerView
private lateinit var mAdapter: FFAdapter
private lateinit var mUser: Users
private val mRefUsers = database.child(NODE_USERS)
private var mListItems = listOf<Users>()

class FollowingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        val uid  = intent.getStringExtra(NODE_UID)
        val mRefMainList = database.child(NODE_FOLLOW).child(uid!!)
        val mRefLastMessages = database.child(NODE_MESSAGES).child(uid)
        database.child(NODE_USERS).child(uid)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                mUser = it.getValue(Users::class.java)!!
                name_following_activity.text = mUser.username.toUpperCase()

            })
        mRecyclerView = following_recycler_view
        mAdapter = FFAdapter()
        // 1 запрос
        mRefMainList.addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getUserModel() }
            mListItems.forEach { model ->

                // 2 запрос
                mRefUsers.child(model.uid)
                    .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getUserModel()

                        // 3 запрос
                        mRefLastMessages.child(model.uid).limitToLast(1)
                            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                                newModel.lastmsg = newModel.state


                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

    }

}

