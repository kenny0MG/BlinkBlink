package org.robotics.blinkblink.Activity.Users

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.robotics.blinkblink.R

class ImagesAdapter(private val images: List<String>,private val listener: Listener) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    interface Listener {
        fun openItem(image: String)



    }


    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.imageitem, parent, false) as ImageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
        holder.itemView.setOnClickListener {
            listener.openItem(holder.absoluteAdapterPosition.toString())
        }
    }



    override fun getItemCount(): Int = images.size
    private fun ImageView.loadImage(image: String) {
        Glide.with(this).load(image).centerCrop().into(this)
    }
}

class SquareImageView(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}