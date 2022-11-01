package org.robotics.blinkblink.Activity.Comments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_home.*
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.OtherUserActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.NODE_USERS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database
import java.util.*

class CommentsActivity : baseActivity(),CommentsAdapter.Listener {
    private lateinit var mAdapter: CommentsAdapter
    private  lateinit var mUser: Users
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        closecomm.setOnClickListener{
            finish()
        }
        database.child(NODE_USERS).child(currentUid()!!)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!

                Glide.with(this).load(mUser.photo).placeholder(R.drawable.person).into(avataryy)
            })
        val postId = intent.getStringExtra(EXTRA_POST_ID) ?: return finish()
        setupAuthGuard {
            mAdapter = CommentsAdapter(this)
            comment_recycle.layoutManager=LinearLayoutManager(this)
            comment_recycle.adapter=mAdapter
            val viewModel = initViewModel<CommentsViewModel>()

            viewModel.init(postId)
            viewModel.comments.observe(this, Observer { it?.let {
                mAdapter.updateComments(it)
            } })
            viewModel.user.observe(this, Observer{ it?.let{
                mUser = it
            } })
            postscomm.setOnClickListener{
                viewModel.createComment(commtext.text.toString(), mUser)
                commtext.setText("")
            }
        }
    }
    companion object{
        private const val EXTRA_POST_ID = "POST_ID"
        fun start(context: Context,postId:String){
            val intent = Intent(context, CommentsActivity ::class.java)
            intent.putExtra(EXTRA_POST_ID,postId)
            context.startActivities(arrayOf(intent))

        }
    }
//обработчик нажатий айтема
    override fun click(uid: String) {
        if(uid == currentUid()!!){
            val intent = Intent(this, UserActivity::class.java)
            startActivities(arrayOf(intent))
        }else {
            val intent = Intent(this, OtherUserActivity::class.java)
            intent.putExtra("uid", uid)


            startActivities(arrayOf(intent))
        }
    }
}


