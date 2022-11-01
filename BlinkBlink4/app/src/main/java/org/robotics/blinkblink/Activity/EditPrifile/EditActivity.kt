package org.robotics.blinkblink.Activity.EditPrifile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

import org.robotics.blinkblink.models.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.edit.*
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.setupAuthGuard
import org.robotics.blinkblink.utils.*
import java.io.IOException
import java.util.*


class EditActivity:baseActivity(){
    private lateinit var mViewModel: EditProfileViewModel
    private val TAG = "EditActivity"
    private lateinit var mUser: Users
    private val TAKE_PICTURE_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 1001
    private var filePath: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit)

        Log.d(TAG, "OnCreate")

        imageView1.setOnClickListener{
            finish()
        }
        saveimage.setOnClickListener{
            updateProfile1()
        }
        change_photo.setOnClickListener{launchGallery() }



        setupAuthGuard {
            mViewModel = initViewModel()
        mViewModel.user.observe(this, androidx.lifecycle.Observer { it.let {
            mUser = it
            name_input8.setText(mUser.name, TextView.BufferType.EDITABLE)
            username_input8.setText(mUser.username, TextView.BufferType.EDITABLE)
            bio_input3.setText(mUser.bio, TextView.BufferType.EDITABLE)
            Glide.with(this).load(mUser.photo).placeholder(R.drawable.person).into(imageView2)
            followersedit.setText(mUser.followers.size.toString(),TextView.BufferType.EDITABLE)
            followinfedit.setText(mUser.following.size.toString(),TextView.BufferType.EDITABLE)
            database.child(NODE_IMAGES).child(currentUid()!!).addValueEventListener(Utils.ValueEventListenerAdapter{
                val images = it.children.map{it.getValue(String::class.java)!!}
                postsedit.setText(images.size.toString(),TextView.BufferType.EDITABLE)

            })
        }
        })
        }




    }


//Новое изображение
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

                uploadImage()
                mUser= mUser.copy(photo = filePath.toString())

                Glide.with(this).load(mUser.photo).into(imageView2)





            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun addUploadRecordToDb(uri: String){

        val data = HashMap<String, Any>()





        database.child(NODE_USERS).child(currentUid()!!).child(CHILD_FILE_URL).setValue(uri)
            .addOnSuccessListener { documentReference ->
                //Picasso.get().load(uri).placeholder(R.drawable.ic_close_foreground).into(imageView2)

                Toast.makeText(this, "Saved image", Toast.LENGTH_LONG).show()



            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving image", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImage(){
        if(filePath != null){
            val uid = currentUid()
            val ref = storage.child("user/$uid/photo")
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    addUploadRecordToDb(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }





    private fun updateProfile1(){
        val user = Users(
            name = name_input8.text.toString(),
            username = username_input8.text.toString().toLowerCase().replace(" ","_"),
            bio = bio_input3.text.toString())


        val error = validate1(user)
        if(error == null){
            updateUsers(user)
        }
        else{
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUsers(user:Users){
        mViewModel.updateUsersProfile(currentUser = mUser,newUser = user).
                addOnSuccessListener{
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                    finish()
                }


    }
//
    private fun validate1(user:Users):String?=
        when{
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"


            else -> null
        }
}










