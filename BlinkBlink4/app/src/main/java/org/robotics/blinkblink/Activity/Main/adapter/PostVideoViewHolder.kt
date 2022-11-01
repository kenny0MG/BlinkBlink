package org.robotics.blinkblink


import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import org.robotics.blinkblink.Activity.Users.UserActivity
import org.robotics.blinkblink.commons.loadUserPhoto
import org.robotics.blinkblink.commons.setCaptionText
import org.robotics.blinkblink.databinding.PostVideoItemBinding

import org.robotics.blinkblink.models.FeedPosts
import org.robotics.blinkblink.utils.currentUid
import org.robotics.blinkblink.utils.fromUrl
import java.util.concurrent.TimeUnit

class PostVideoViewHolder(private val binding: PostVideoItemBinding) :
    RecyclerView.ViewHolder(binding.root), VideoPlayerEventListener,  CompoundButton.OnCheckedChangeListener{
    private var item: FeedPosts? = null

    fun bind(videoItem: FeedPosts) {
        item = videoItem

        binding.avatarPostsVideo.loadUserPhoto(videoItem.photo)
        binding.namePostsVideo.text = videoItem.username
        binding.timeVideo.setCaptionText(videoItem.fileStemp())


        val usernameSpann = SpannableString(videoItem.username)
        usernameSpann.setSpan(StyleSpan(Typeface.BOLD),
            0,
            usernameSpann.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpann.setSpan(RelativeSizeSpan(1.06f),0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpann.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if(videoItem.uid == currentUid()!!){
                    val intent = Intent(widget.context, UserActivity::class.java)
                    widget.context.startActivities(arrayOf(intent))
                }else{
                    val intent = Intent(widget.context, OtherUserActivity ::class.java)
                    intent.putExtra("uid",videoItem.uid)
                    widget.context.startActivities(arrayOf(intent))
                }


            }

            override fun updateDrawState(ds: TextPaint) {

            }

        }, 0, usernameSpann.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)



        if (videoItem.caption == "") {
            binding.captionTextVideo.visibility = View.GONE
        } else {
            binding.captionTextVideo.visibility = View.VISIBLE
            binding.captionTextVideo.text =
                SpannableStringBuilder().append(usernameSpann).append(" ").append(videoItem.caption)
            binding.captionTextVideo.movementMethod = LinkMovementMethod.getInstance()


        }

        Glide.with(itemView.context)
            .load(videoItem.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.itemVideoPlayerThumbnail)



        binding.checkboxVolume.setOnCheckedChangeListener(this)

    }

    override fun onLoading(isLoading: Boolean) {
        binding.progressCircular.isVisible = isLoading
    }

    override fun onPrePlay(player: SimpleExoPlayer) {
        binding.itemVideoPlayer.visibility = View.GONE
        binding.itemVideoPlayerThumbnail.visibility = View.VISIBLE
        binding.timeLeft.visibility = View.GONE
        binding.checkboxVolume.visibility = View.GONE
        //play video
        with(player) {
            playVideo()
            binding.itemVideoPlayer.player = this
        }
    }

    override fun onPlayCanceled() {
        onLoading(false)
        binding.timeLeft.visibility = View.GONE
        binding.checkboxVolume.visibility = View.GONE

        binding.itemVideoPlayer.player?.stop()
        binding.itemVideoPlayer.player = null

        binding.itemVideoPlayer.visibility = View.GONE
        binding.itemVideoPlayerThumbnail.visibility = View.VISIBLE
    }

    private fun getTimeLeft() {
        binding.root.postDelayed({
            binding.timeLeft.text = calculateTimeLeft(
                binding.itemVideoPlayer.player?.contentDuration?.minus(
                    binding.itemVideoPlayer.player?.currentPosition ?: 0
                ) ?: 0
            )
            getTimeLeft()
        }, 1000)
    }

    override fun onPlay() {
        getTimeLeft()

        binding.root.postDelayed({
            if (binding.itemVideoPlayer.player != null) {
                binding.checkboxVolume.setOnCheckedChangeListener(null)
                binding.checkboxVolume.isChecked = binding.itemVideoPlayer.player!!.volume  != 1f
                binding.checkboxVolume.setOnCheckedChangeListener(this)

                binding.timeLeft.visibility = View.VISIBLE
                binding.checkboxVolume.visibility = View.VISIBLE
                binding.itemVideoPlayer.visibility = View.VISIBLE
                binding.itemVideoPlayerThumbnail.visibility = View.INVISIBLE
            }
        }, DELAY_BEFORE_HIDE_THUMBNAIL) // wait to be sure the texture view is render
    }

    private fun SimpleExoPlayer.playVideo() {
        stop(true)
        val videoUrl = item?.image ?: return
        setMediaItem(MediaItem.fromUri(videoUrl))
        prepare()
    }

    companion object {
        private const val DELAY_BEFORE_HIDE_THUMBNAIL = 500L
    }

    fun calculateTimeLeft(timeLeft: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(timeLeft),
            TimeUnit.MILLISECONDS.toSeconds(timeLeft)
        )
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        binding.itemVideoPlayer.player?.volume = if (isChecked) 0f else 1f
    }
}