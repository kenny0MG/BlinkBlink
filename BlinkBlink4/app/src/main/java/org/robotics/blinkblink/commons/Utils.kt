package org.robotics.blinkblink.commons

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.main_item.*
import org.robotics.blinkblink.Activity.Groups.MakeGroups.MakeGroupsActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.Activity.Main.MainActivity
import org.robotics.blinkblink.models.CommonModel
import org.robotics.blinkblink.models.Users
import org.robotics.blinkblink.utils.NODE_MAIN_LIST
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.database
import java.text.SimpleDateFormat
import java.util.*
lateinit var APP_ACTIVITY:MakeGroupsActivity
const val TYPE_TEXT = "text"
class Utils {
    class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ValueEventListener {
        private val TAG = "ValueEventListenerAdapt"

        override fun onDataChange(data: DataSnapshot) {
            handler(data)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(TAG, "onCancelled: ", error.toException())
        }
    }

    class AppValueEventListener (val onSuccess:(DataSnapshot) -> Unit) :ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            onSuccess(p0)
        }

    }

}
    fun coordinateBtnandInput(btn: AppCompatButton, vararg inputs: EditText){
    val watcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            btn.isEnabled=inputs.all { it.text.isNotEmpty() }
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }
    inputs.forEach{it.addTextChangedListener(watcher)}
    btn.isEnabled=inputs.all { it.text.isNotEmpty() }


}

fun coordinateBtnandInput2(btn: ImageView, vararg inputs: EditText){
    val watcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            btn.isEnabled=inputs.all { it.text.isNotEmpty() }
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }
    inputs.forEach{it.addTextChangedListener(watcher)}
    btn.isEnabled=inputs.all { it.text.isNotEmpty() }


}
 fun ImageView.loadImage(image: String?) {
    Glide.with(this).load(image).centerCrop().into(this)
}


fun ImageView.loadUserPhoto(photoUrl: String?) =
        Glide.with(this).load(photoUrl).placeholder(R.drawable.person).into(this)

fun TextView.setCaptionText2(username: String, caption: String, date: Date? = null) {
    val usernameSpannable = SpannableString(username)
    usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    usernameSpannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
        }

        override fun updateDrawState(ds: TextPaint) {}
    }, 0, usernameSpannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val dateSpannable = date?.let{
        val dateText = formatRelativeTimestamp(date, Date())
        val spannableString = SpannableString(dateText)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.blacko)),
            0, dateText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString
    }

    text = SpannableStringBuilder().apply {
        append(usernameSpannable)
        append(" ")
        append(caption)
        dateSpannable?.let{
            append(" ")
            append(it)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setCaptionText( date: Date? = null) {


        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

    val dateSpannable = date?.let{
        val dateText = formatRelativeTimestamp(date, Date())
        val spannableString = SpannableString(dateText)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.blacko)),
            0, dateText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString
    }

    text = SpannableStringBuilder().apply {

        append()

        dateSpannable?.let{
            append(" ")
            append(it)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}

fun ImageView.loadImageOrHide(image: String?) =
    if (image != null) {
        visibility = View.VISIBLE
        loadImage(image)
    } else {
        visibility = View.GONE
    }
fun DataSnapshot.getUserModel(): Users =
    getValue(Users::class.java) ?: Users()
fun DataSnapshot.getCommonModel(): CommonModel =
    getValue(CommonModel::class.java) ?: CommonModel()
fun showToast(message:String){
    lateinit var APP_ACTIVITY: MainActivity
    /* Функция показывает сообщение */
    Toast.makeText(APP_ACTIVITY,message,Toast.LENGTH_SHORT).show()
}
fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}
fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }


}
