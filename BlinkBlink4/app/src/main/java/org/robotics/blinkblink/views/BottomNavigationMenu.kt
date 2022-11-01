package org.robotics.blinkblink.views

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.robotics.blinkblink.*
import org.robotics.blinkblink.Activity.Chats.HomeActivity
import org.robotics.blinkblink.Activity.Main.MainActivity
import org.robotics.blinkblink.Activity.Users.UserActivity

class BottomNavigationMenu(private val bnv: BottomNavigationView, activity: Activity, val onNumber: Int):LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        bnv.menu.getItem(onNumber).isChecked=true
    }
    init{
        bnv.setOnNavigationItemSelectedListener {
            val NextActivity=
                when (it.itemId) {
                    R.id.nav_home -> HomeActivity::class.java;
                    R.id.nav_chat -> MainActivity::class.java
                    R.id.nav_person -> UserActivity::class.java
                    else ->{
                        Log.e(ContentValues.TAG,"uknown nav item click $it")
                        null
                    }
                }
            if(NextActivity != null){

                val intent = Intent(activity,NextActivity)
                intent.flags= Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.startActivity(intent)
                activity.overridePendingTransition(0,0)
                true
            }
            else{
                false
            }


        }


    }

}

fun baseActivity.setupButtonNavigations(navNumber:Int){
    val bnv =org.robotics.blinkblink.views.BottomNavigationMenu(bottom_navigation_view,this,navNumber)
    this.lifecycle.addObserver(bnv)
}
