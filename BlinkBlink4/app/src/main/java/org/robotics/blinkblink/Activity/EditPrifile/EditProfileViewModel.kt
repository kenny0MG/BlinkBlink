package org.robotics.blinkblink.Activity.EditPrifile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import org.robotics.blinkblink.data.UsersRepository
import org.robotics.blinkblink.models.Users

class EditProfileViewModel(private val onFailureListener: OnFailureListener,private val usersRepository: UsersRepository): ViewModel() {
    fun updateUsersProfile(currentUser: Users, newUser: Users): Task<Unit> =
        usersRepository.updateUsersProfile(currentUser=currentUser,newUser=newUser).addOnFailureListener(onFailureListener)


    val user: LiveData<Users> = usersRepository.getUser()





}