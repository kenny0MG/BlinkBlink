package org.robotics.blinkblink.Activity.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_number_pochta_nameauth.*
import org.robotics.blinkblink.commons.coordinateBtnandInput
import org.robotics.blinkblink.Activity.Main.MainActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.setupAuthGuard

class NumberPochtaNameauth : baseActivity(),  View.OnClickListener {
    private lateinit var mViewModel: LoginViewModel
    private lateinit var mFirebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_pochta_nameauth)

        mFirebaseAuth = FirebaseAuth.getInstance()

        coordinateBtnandInput(button8,email_input,passwd_input)
        imageView9.setOnClickListener{
            finish()
        }
        button8.setOnClickListener(this)

    }
    override fun onClick(p0: View) {
        //mViewModel.onLoginClick()
        val email =email_input.text.toString()
        val passwd = passwd_input.text.toString()
        if(validate(email, passwd)){
            mFirebaseAuth.signInWithEmailAndPassword(email,passwd).addOnCompleteListener{
                if(it.isSuccessful){
                    finish()
                    startActivity(Intent(this,
                        MainActivity::class.java))

                }
                else{
                    email_input?.error = ""
                    passwd_input?.error = "Пароль или логин не совпадает"
                    return@addOnCompleteListener
                }
            }
        }
    }






    private fun validate(email:String,passwd:String) =
        email.isNotEmpty() && passwd.isNotEmpty()

}