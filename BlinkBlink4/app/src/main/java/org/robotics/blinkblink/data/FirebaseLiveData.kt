package org.robotics.blinkblink.data

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import org.robotics.blinkblink.commons.Utils

class FirebaseLiveData(private val query: Query): LiveData<DataSnapshot>(){
    private val listener = Utils.ValueEventListenerAdapter {
        value = it
    }
    override fun onActive() {
        super.onActive()
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }
}