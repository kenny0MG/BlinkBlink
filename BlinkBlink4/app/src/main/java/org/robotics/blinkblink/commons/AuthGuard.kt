package org.robotics.blinkblink.commons

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.utils.auth

class AuthGuard(private val activity: baseActivity,f: (String) -> Unit): LifecycleObserver {

    init{

        val user = auth.currentUser
        if(user == null){
            activity.goToLogin()
        }else{
            f(user.uid)
            activity.lifecycle.addObserver(this)
        }
    }

    private val listener = FirebaseAuth.AuthStateListener{
        if (it.currentUser == null) {
            activity.goToLogin()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        auth.addAuthStateListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)

    fun onStop(){
        auth.removeAuthStateListener(listener)
    }
}

fun baseActivity.setupAuthGuard(f: (String) -> Unit){
    AuthGuard(this,f)
}