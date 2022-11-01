package org.robotics.blinkblink

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.bottomsheet_fragment.*
import kotlinx.android.synthetic.main.fragment_bottom_more.*
import org.robotics.blinkblink.commons.coordinateBtnandInput
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.currentUid

private lateinit var mUser: Users

class BottomMoreFragment(private val uid:String,id:String) :  BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_bottom_more, container, false)
        val btn_complaint = view.findViewById(R.id.button_complaint) as Button
        val btn_black_list = view.findViewById(R.id.button_black_list) as Button
        val btn_delete_post = view.findViewById(R.id.button_delete_post) as Button
        val btn_add_to_archive = view.findViewById(R.id.button_add_to_archive) as Button
        val btn_delete_comments = view.findViewById(R.id.button_delete_comments) as Button

        if(uid == currentUid()!!){
            btn_complaint.visibility = View.GONE
            btn_black_list.visibility = View.GONE
            btn_delete_post.visibility = View.VISIBLE
            btn_add_to_archive.visibility = View.VISIBLE
            btn_delete_comments.visibility = View.VISIBLE


        }
        else{
            btn_complaint.visibility = View.VISIBLE
            btn_black_list.visibility = View.VISIBLE
            btn_delete_post.visibility = View.GONE
            btn_add_to_archive.visibility = View.GONE
            btn_delete_comments.visibility = View.GONE
        }
        return view
    }

    companion object {

    }
}