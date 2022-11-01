package org.robotics.blinkblink.commons

import com.google.android.gms.tasks.Task
import org.robotics.blinkblink.utils.auth

class FirebaseAuthManager: AuthManager {
    override fun signOut() {
        auth.signOut()
    }

    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()

}


