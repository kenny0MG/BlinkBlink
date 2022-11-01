package org.robotics.blinkblink.Activity.Users

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.user.*
import org.robotics.blinkblink.Activity.ImageListPerson.ImagesActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.NODE_FAVORITE_IMAGE
import org.robotics.blinkblink.utils.NODE_USERS
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database

private lateinit var mUser: Users
class FavoriteFragment : Fragment(), ImagesAdapter.Listener {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        database.child(NODE_FAVORITE_IMAGE).child(currentUid()!!)
            .addValueEventListener(Utils.ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
                feed_favorite_post.adapter = ImagesAdapter(images,this)
                feed_favorite_post.layoutManager = GridLayoutManager(context, 3)


            })
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    companion object {

    }

    override fun openItem(image: String) {

        val intent = Intent(context, ImagesActivity ::class.java)
        intent.putExtra("image",image)
        intent.putExtra("uid", currentUid()!!)


        context?.startActivities(arrayOf(intent))
    }
}