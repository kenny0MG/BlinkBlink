package org.robotics.blinkblink.Activity.AddFriends

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_addfriends.*
import org.robotics.blinkblink.*
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database


//following - подписки followers - подписчики
//Класс добавление и поиска друзей

class addFriendsActivity: baseActivity(), FriendsAdapter.Listener, TextWatcher {


    private lateinit var mUser: Users
    private lateinit var mUsers: List<Users>
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mViewModel: AddFriendViewModel
    private lateinit var mRecyclerView: RecyclerView
    private var isSearchEntered = false
    private var isSearch = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfriends)
        closee.setOnClickListener{
            finish()
        }
        mRecyclerView=addfriendsview
        setupAuthGuard {
             mAdapter= FriendsAdapter(this)
            mViewModel = initViewModel()
            mRecyclerView.adapter = mAdapter
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.isNestedScrollingEnabled = false
            mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewModel.userAndFriends.observe(this, Observer {it?.let { (user, otherUsers) ->
            mUser = user
            mUsers = otherUsers
            mAdapter.update(mUsers, mUser.following)


        }

            searchnext.addTextChangedListener(this)
            mViewModel.setSearchText("")
        })

    }
    }



    //подписка
    override fun follow(uid: String) {
        setFollow(uid,true){
            mAdapter.followed(uid)
        }


    }


    //отписка
    override fun unfollow(uid: String) {
        setFollow(uid,false){
            mAdapter.unfollowed(uid)
        }

    }
//Обработчик нажатий
    override fun click(uid:String) {
        if(uid == currentUid()!!){
            val intent = Intent(this, UserActivity::class.java)
            startActivities(arrayOf(intent))
        }else {
            val intent = Intent(this, OtherUserActivity::class.java)
            intent.putExtra("uid", uid)


            startActivities(arrayOf(intent))
        }
    }

    private fun setFollow(uid:String, follow: Boolean, onSucces:()->Unit){
        mViewModel.setFollow( mUser.uid,uid,follow).addOnSuccessListener {
            onSucces()
        }.addOnFailureListener{
            it!!.message!!
        }


    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        if(!isSearch){
            isSearch = true
            Handler().postDelayed({
                isSearch = false
                mViewModel.setSearchText(searchnext.text.toString().toLowerCase())
            },500)

        }

    }


}


