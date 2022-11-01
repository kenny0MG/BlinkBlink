package org.robotics.blinkblink.commons

import com.google.android.gms.tasks.Task

interface AuthManager {
    fun signOut()
    fun signIn(email: String, password: String): Task<Unit>
}