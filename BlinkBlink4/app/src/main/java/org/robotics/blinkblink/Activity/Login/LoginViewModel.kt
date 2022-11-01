package org.robotics.blinkblink.Activity.Login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import kotlinx.android.synthetic.main.activity_number_pochta_nameauth.*
import org.robotics.blinkblink.Activity.Main.MainActivity
import org.robotics.blinkblink.commons.*

class LoginViewModel(private val authManager: AuthManager,
                     private val app: BlinkApp,
                     private val commonViewModel: CommonViewModel,
                     private val onFailureListener: OnFailureListener
): ViewModel() {

    private val _goToHomeScreen = SingleLiveEvent<Unit>()
    val goToHomeScreen: LiveData<Unit> = _goToHomeScreen
    fun onLoginClick(email: String, passwd: String) {
        if (validate(email, passwd)) {
            authManager.signIn(email,passwd).addOnSuccessListener {
                _goToHomeScreen.value=Unit
            }
        }
    }
    private fun validate(email:String,passwd:String) =
        email.isNotEmpty() && passwd.isNotEmpty()
}