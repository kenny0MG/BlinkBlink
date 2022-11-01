package org.robotics.blinkblink

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.robotics.blinkblink.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_passwordusername.*
import kotlinx.android.synthetic.main.user.*
import org.robotics.blinkblink.commons.coordinateBtnandInput
import org.robotics.blinkblink.Activity.Main.MainActivity
import org.robotics.blinkblink.utils.NODE_USERS
import org.robotics.blinkblink.utils.auth
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database

class PoctaNomerRegistrActivity : AppCompatActivity(), EmailFragment.Listener, NamePass.Listener {


    private val TAG = "RegisterActivity"

    private var mEmail: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocta_nomer_registr)

        if(savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction().add(R.id.frame_loyaot, EmailFragment()).commit()
        }

    }

    override fun onNext(email: String) {

        if(email.isNotEmpty()){

            mEmail=email
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener{
                if(it.isSuccessful){
                    if(it.result.signInMethods?.isNotEmpty() == false){
                        supportFragmentManager.beginTransaction().replace(R.id.frame_loyaot,
                            NamePass()).addToBackStack(null).commit()

                    }
                    else{
                        email_input3?.error = "This email is already exists"
                        return@addOnCompleteListener
                    }
                }
                else{
                    email_input3?.error =it.exception!!.message!!

                }
            }


        }
        else{
            email_input3?.error = "Enter email"
            return
        }
    }

    override fun onRegister(fullName: String, password: String) {


        if(fullName.isNotEmpty() && password.isNotEmpty()){
            val email = mEmail

            if(email != null ) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){

                        val user = MkUser(fullName,email, currentUid()!!)
                        database.child(NODE_USERS).child(it.result.user!!.uid).setValue(user).addOnCompleteListener{
                            if(it.isSuccessful){
                                startActivity(Intent(this,
                                    MainActivity::class.java))
                                finish()
                            }
                            else{
                                Log.e(TAG,"failed to create user profile")
                                Toast.makeText(this, "Something wrong happend. Please tru again latter", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else{
                        Log.e(TAG,"failed to create user ")
                        Toast.makeText(this, "Something wrong happend. Please tru again latter", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Log.e(TAG,"onRegister: email is null")

                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                supportFragmentManager.popBackStack()

            }

        }
        else{
            username_input?.error = ""
            passwd_input2?.error = "Заполните поля полностью"
            return
        }
    }
    private fun MkUser(fullName: String, email: String,uid: String):Users{
        val username = mkUsername(fullName)


        return Users(name = fullName,username = username,email = email, uid = uid)
    }

    private fun mkUsername(fullName: String)=
        fullName.toLowerCase().replace(" ","_")





}

class EmailFragment: Fragment(){
    private lateinit var mListener: Listener
    interface Listener{
        fun onNext(email: String)



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_register_email,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnandInput(nextbtn,email_editText)
        super.onViewCreated(view, savedInstanceState)


        nextbtn.setOnClickListener{
            val email = email_editText.text.toString()
            mListener.onNext(email)

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mListener=context as Listener



    }
}


class NamePass:Fragment(){
    private lateinit var mListener: Listener
    interface Listener{
        fun onRegister(fullName: String, password:String)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_passwordusername,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnandInput(registerbtn,username_editText,passwd_editText)

        registerbtn.setOnClickListener{

            val fullname = username_editText.text.toString()
            val password = passwd_editText.text.toString()
            mListener.onRegister(fullname,password)

        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener=context as Listener


    }
}