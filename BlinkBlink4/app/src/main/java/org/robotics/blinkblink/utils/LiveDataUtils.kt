package org.robotics.blinkblink.utils

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import org.robotics.blinkblink.data.FirebaseLiveData


fun DatabaseReference.liveData(): LiveData<DataSnapshot> = FirebaseLiveData(this)