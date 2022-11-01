package org.robotics.blinkblink.Activity.Groups.MakeGroups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_make_groups.*
import org.robotics.blinkblink.Activity.Chats.HomeActivity
import org.robotics.blinkblink.Activity.Groups.AddContactsActivity.Companion.listContacts
import org.robotics.blinkblink.Activity.Groups.ChatAdapter
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.showToast
import org.robotics.blinkblink.models.Users

import org.robotics.blinkblink.utils.*
import java.io.IOException

private var mUri = Uri.EMPTY
private val PICK_IMAGE_REQUEST = 1001

class MakeGroupsActivity() : baseActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_groups)
        //create_group_btn_complete.setOnClickListener { showToast("Click") }
        closegroup.setOnClickListener{
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        create_group_photo.setOnClickListener { addPhoto()  }
        create_group_btn_complete.setOnClickListener {
            val nameGroup = create_group_input_name.text.toString()
            if (nameGroup.isEmpty()){
                Toast.makeText(this, "Write name", Toast.LENGTH_SHORT).show()
            } else {
                createGroupToDatabase(nameGroup,mUri,listContacts){
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        initRecyclerView()
        create_group_input_name.requestFocus()
    }

    private fun createGroupToDatabase(nameGroup: String, uri: Uri, listContacts: MutableList<Users>, function: () -> Unit) {
        val keyGroup = database.child(NODE_GROUPS).push().key.toString()
        val path = database.child(NODE_GROUPS).child(keyGroup)
        val pathStorage = storage.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

        val mapData = hashMapOf<String, Any>()
        mapData[CHILD_ID] = keyGroup
        mapData[CHILD_USERNAME] = nameGroup
        val mapMembers = hashMapOf<String, Any>()
        listContacts.forEach {
            mapMembers[it.uid] = USER_MEMBER
        }
        mapMembers[currentUid()!!] = USER_CREATOR

        mapData[NODE_MEMBERS] = mapMembers

        path.updateChildren(mapData)
            .addOnSuccessListener {

                if (uri != Uri.EMPTY) {
                    putFileToStorage(uri, pathStorage) {
                        getUrlFromStorage(pathStorage) {
                            path.child(CHILD_FILE_URL).setValue(it)
                        }
                    }
                }
                addGroupsToMainList(mapData, listContacts) {
                    function()
                }

            }
            .addOnFailureListener { Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show() }


    }


    private fun addPhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_recycle_view
        mAdapter = ChatAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {  mAdapter.updateListItems(it) }
        mRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            mUri = data.data

            try {
                Glide.with(this).load(mUri).centerCrop().into(create_group_photo)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
        /* Функция высшего порядка, отправляет картинку в хранилище */
        path.putFile(uri)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }

    }
    inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
        /* Функция высшего порядка, получает  URL картинки из хранилища */
        path.downloadUrl
            .addOnSuccessListener { function(it.toString()) }
            .addOnFailureListener {  }
    }
    fun addGroupsToMainList(
        mapData: HashMap<String, Any>,
        listContacts: MutableList<Users>,
        function: () -> Unit
    ) {
        val path = database.child(NODE_MAIN_LIST)
        val map = hashMapOf<String, Any>()

        map[CHILD_ID] = mapData[CHILD_ID].toString()
        map[CHILD_TYPE] = NODE_GROUPS
        listContacts.forEach {
            path.child(it.uid).child(map[CHILD_ID].toString()).updateChildren(map)
        }
        path.child(currentUid()!!).child(map[CHILD_ID].toString()).updateChildren(map)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }

    }
}

