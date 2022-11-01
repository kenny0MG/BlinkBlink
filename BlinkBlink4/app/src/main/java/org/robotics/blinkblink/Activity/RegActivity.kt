package org.robotics.blinkblink.Activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import kotlinx.android.synthetic.main.activity_reg.*
import org.robotics.blinkblink.Activity.Login.NumberPochtaNameauth
import org.robotics.blinkblink.PoctaNomerRegistrActivity
import org.robotics.blinkblink.R
import org.robotics.blinkblink.baseActivity
import org.robotics.blinkblink.commons.setupAuthGuard

class RegActivity : baseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        buttonNumberNamePochtareg1.setOnClickListener{
                val intent = Intent(this, PoctaNomerRegistrActivity::class.java)
                startActivity(intent)
        }



        vxod.setOnClickListener{
            it.roll()
            val intent = Intent(this, NumberPochtaNameauth::class.java)
            startActivity(intent)


        }
    }
    fun View.roll(shrinkFactor: Float = 0.8f, duration: Long = 500) {
        val downscaleAnimator = AnimatorSet()
        downscaleAnimator.playTogether(
            ObjectAnimator.ofFloat(this, "scaleX", 1f, shrinkFactor),
            ObjectAnimator.ofFloat(this, "scaleY", 1f, shrinkFactor))

        val rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", 360f)

        val upscaleAnimator = AnimatorSet()
        upscaleAnimator.playTogether(
            ObjectAnimator.ofFloat(this, "scaleX", shrinkFactor, 1f),
            ObjectAnimator.ofFloat(this, "scaleY", shrinkFactor, 1f))

        val combinedAnimator = AnimatorSet()
        combinedAnimator.playSequentially(downscaleAnimator, rotationAnimator, upscaleAnimator)
        combinedAnimator.duration = duration
        combinedAnimator.doOnEnd { this.rotation = 0f }
        combinedAnimator.start()
    }

}