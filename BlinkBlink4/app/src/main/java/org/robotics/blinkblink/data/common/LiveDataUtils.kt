package org.robotics.blinkblink.data.common

import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import org.robotics.blinkblink.models.Users

fun <A, B> LiveData<A>.map(f: (A) -> B): LiveData<B> =
    Transformations.map(this, f)



fun <T> LiveData<T>.observeFirstNotNull(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner, object: Observer<T> {
        override fun onChanged(value: T?) {
            value?.let{
                observer(value)
                removeObserver(this)
            }
        }
    })
}

fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}
fun <A, B> LiveData<A>.zip(b: LiveData<B>): LiveData<Pair<A, B>> = zipLiveData(this, b)
