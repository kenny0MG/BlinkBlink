package org.robotics.blinkblink.commons

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks

fun <T> task(block:(TaskCompletionSource<T>) -> Unit): Task<T> {
    val TaskSource = TaskCompletionSource<T>()
    block(TaskSource)
    return TaskSource.task
}
fun Task<*>.toUnit(): Task<Unit> =
    onSuccessTask { Tasks.forResult(Unit) }