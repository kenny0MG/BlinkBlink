package org.robotics.blinkblink.Activity.Users

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_images_post.*
import org.robotics.blinkblink.Activity.ImageListPerson.ImagesActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.utils.NODE_IMAGES
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database
import java.util.*


class FeedImageFragment : Fragment(), ImagesAdapter.Listener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        database.child(NODE_IMAGES).child(currentUid()!!)
            .addValueEventListener(Utils.ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
                Collections.reverse(images)
                feed_image_post.adapter = ImagesAdapter(images,this)
                feed_image_post.layoutManager = GridLayoutManager(getContext(), 3)


            })
        return inflater.inflate(R.layout.fragment_images_post, container, false)
    }


    companion object {

    }

    override fun openItem(image: String) {
        val intent = Intent(context, ImagesActivity ::class.java)
        intent.putExtra("image",image)
        intent.putExtra("uid", currentUid()!!)


        requireContext().startActivities(arrayOf(intent))
    }
}