package org.robotics.blinkblink

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.main_item.*
import org.robotics.blinkblink.commons.BlinkApp
import org.robotics.blinkblink.commons.CommonViewModel

import org.robotics.blinkblink.commons.ModelFactory
import org.robotics.blinkblink.Activity.RegActivity
import org.robotics.blinkblink.utils.currentUid

abstract class baseActivity:AppCompatActivity() {


    lateinit var commonViewModel: CommonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        commonViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
        commonViewModel.errorMessage.observe(this, Observer {
            it.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
    protected inline fun <reified T : ViewModel> initViewModel(): T =
        ViewModelProviders.of(this, ModelFactory(BlinkApp(), commonViewModel,
            commonViewModel)).get(T::class.java)

    fun goToLogin() {
        startActivity(Intent(this, RegActivity::class.java))
        finish()
    }
//    fun newMsgCircle(id: String, uid: String, read: Boolean)
//    {
//        if(uid != currentUid()!!){
//            if(!read){
//                circle_new_message.visibility = View.VISIBLE
//            }
//        }
//        else{
//            circle_new_message.visibility = View.GONE
//        }
//    }

    companion object {
        const val TAG = "BaseActivity"
    }

    }

