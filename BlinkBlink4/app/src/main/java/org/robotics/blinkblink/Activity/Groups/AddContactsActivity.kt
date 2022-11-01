package org.robotics.blinkblink.Activity.Groups



import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_contacts.*
import org.robotics.blinkblink.Activity.Groups.MakeGroups.MakeGroupsActivity

import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.getCommonModel
import org.robotics.blinkblink.commons.getUserModel
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.*

class AddContactsActivity : baseActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ChatAdapter
    private val mRefMainList = database.child(NODE_FOLLOW).child(currentUid()!!)
    private val mRefUsers = database.child(NODE_USERS)
    private val mRefLastMessages = database.child(NODE_MESSAGES).child(currentUid()!!)
    private var mListItems = listOf<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacts)
        closeaddgrp.setOnClickListener{
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        listContacts.clear()
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            if (listContacts.isEmpty()) {
                Toast.makeText(this, "Добавьте участника", Toast.LENGTH_SHORT).show()
            }else {
                val intent = Intent(applicationContext, MakeGroupsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = recycler_add_contacts
        mAdapter = ChatAdapter()
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

    companion object{
        val listContacts = mutableListOf<Users>()
    }
}


