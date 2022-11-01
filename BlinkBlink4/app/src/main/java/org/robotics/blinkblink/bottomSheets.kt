package org.robotics.blinkblink

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.models.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.bottomsheet_fragment.*
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.coordinateBtnandInput
import org.robotics.blinkblink.commons.task
import org.robotics.blinkblink.utils.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

private val PICK_IMAGE_REQUEST = 1
private lateinit var mUser: Users

private var filePath: Uri? = null
private lateinit var mStorage: StorageReference
private val TAKE_PICTURE_REQUEST_CODE = 1

class bottomSheets: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        database.child(NODE_USERS).child(currentUid()!!).addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

            mUser = it.getValue(Users::class.java)!!


        })


        return inflater.inflate(R.layout.bottomsheet_fragment,container,false)
    }


    private fun launchGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data

            try {
                Glide.with(this).load(filePath).centerCrop().into(imageViewposts)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage(){
        if(filePath != null){
            val ref = storage.child("user/${currentUid()!!}/images/$filePath")
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        addUploadRecordToDb(downloadUri.toString())
                    }
                }
        }
    }

    private fun addUploadRecordToDb(uri: String ){


        val timestemp = ServerValue.TIMESTAMP
        val uid = currentUid()!!


        database?.child("feed-posts").child(uid).push().setValue(mkFeedPost(uid,uri


        )).addOnSuccessListener {
            database.child(NODE_USERS).child(currentUid()!!).addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {

                mUser = it.getValue(Users::class.java)!!
                if(mUser.typeProfile){

                    copyFeedPostsEvery(postsAuthorUid = uid,uid =currentUid()!!)
                    copyPosts(uid, currentUid()!!)
                }else{
                    copyFeedPostsRec(postsAuthorUid = uid,uid = currentUid()!! )
                    copyFeedPostsEvery(postsAuthorUid = uid,uid =currentUid()!!)
                    copyPosts(uid, currentUid()!!)
                }

            })
            }






        database?.child("images").child(currentUid()!!).push().setValue(uri,timestemp)
            .addOnSuccessListener {

            }


    }
    private fun mkFeedPost(uid:String,uri:String): FeedPosts {
        return FeedPosts(
            uid = uid,
            username = mUser.username,
            image = uri,
            name = mUser.name,
            caption = caption_input.text.toString(),
            photo = mUser.photo
        )

        }


    fun imageReaderNew(root: File) {
        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()

        if (listAllFiles != null && listAllFiles.size > 0) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".jpeg")) {
                    // File absolute path
                    Log.e("downloadFilePath", currentFile.getAbsolutePath())
                    // File Name
                    Log.e("downloadFileName", currentFile.getName())
                    fileList.add(currentFile.absoluteFile)

                }
            }
            Log.w("fileList", "" + fileList.size)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        buttonimagepost.setOnClickListener{
            launchGallery()
        }

        buttonposts.setOnClickListener{uploadImage()}


        //coordinateBtnandInput(buttonposts,caption_input)
        if(imageViewposts.drawable!=null)
        {
            buttonposts.isEnabled
        }

        super.onViewCreated(view, savedInstanceState)
    }
    private fun copyRecord(fromPath: Query, toPath: DatabaseReference) {
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toPath.setValue(dataSnapshot.value).addOnCompleteListener { task ->
                    if (task.isComplete) {
                        println("Complete")
                    } else {
                        println("Failed")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        fromPath.addListenerForSingleValueEvent(valueEventListener)
    }
    fun copyFeedPostsRec(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child("recommendation-post").updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))

                })
        }
     fun copyFeedPostsEvery(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }
    fun copyPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child("post").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }

}


