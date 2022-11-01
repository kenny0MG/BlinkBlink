package org.robotics.blinkblink.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.robotics.blinkblink.R


fun ImageView.fromUrl(url: String?) {
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(false)
        .encodeQuality(100)
        .format(DecodeFormat.PREFER_ARGB_8888)
        .placeholder(R.drawable.empty_box)
        .into(this)
}