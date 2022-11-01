package org.robotics.blinkblink.Activity.Main

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import org.robotics.blinkblink.commons.Utils
import org.robotics.blinkblink.commons.task
import org.robotics.blinkblink.utils.*

fun copyImage(postsAuthorUid: String, uid: String): Task<Unit> =
    task { taskSource ->
        database.child(NODE_IMAGES).child(uid)
            .equalTo(postsAuthorUid)
            .addListenerForSingleValueEvent(Utils.ValueEventListenerAdapter {
                val postsMap = it.children.map { it.key to it.value }.toMap()
                database.child(NODE_FAVORITE).updateChildren(postsMap)
                    .toUnit()
                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
            })
    }