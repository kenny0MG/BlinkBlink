package org.robotics.blinkblink

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import org.robotics.blinkblink.Activity.Main.FeedPostFavorite
import org.robotics.blinkblink.Activity.Main.FeedPostLikes
import org.robotics.blinkblink.data.PostType
import org.robotics.blinkblink.databinding.PostImageItemBinding
import org.robotics.blinkblink.databinding.PostVideoItemBinding
import org.robotics.blinkblink.models.FeedPosts


class PostListAdapter(private val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
    {
    private var dataSet: MutableList<FeedPosts> = mutableListOf()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
        private var postfavorite: Map<Int, FeedPostFavorite> = emptyMap()


    fun updateDataSet(dataSet: List<FeedPosts>) {
        val diffCallback = DiffCallback(this.dataSet, dataSet)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataSet.clear()
        this.dataSet.addAll(dataSet)
        diffResult.dispatchUpdatesTo(this)
    }

    interface Listener {
        fun toogleLike(postId: String)
        fun loadlikes(postId: String, position: Int)
        fun openComm(postId: String)
        fun tooglefavorite(postId: String,image:String)
        fun loadfavorite(postId: String, position: Int)
        fun openbottom(postId: String,uid:String)
        fun openchats(postId: String,userId: String,username:String,postImage:String,userImage:String)


    }
        fun updatePostLikes(position: Int, likes: FeedPostLikes) {
            postLikes += (position to likes)
            notifyItemChanged(position)
        }
        fun updatePostIzbr(position: Int, izbr: FeedPostFavorite) {
            postfavorite += (position to izbr)
            notifyItemChanged(position)
        }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {


        when (viewType) {
            PostType.IMAGE.typeId -> {
                return PostImageViewHolder(
                    PostImageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            PostType.VIDEO.typeId -> {
                return PostVideoViewHolder(
                    PostVideoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw java.lang.IllegalArgumentException("No valid post type")
            }
        }

    }

    override fun getItemViewType(position: Int): Int = dataSet[position].type

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (dataSet[position].type == PostType.IMAGE.typeId){
            (holder as PostImageViewHolder).bind(dataSet[position], postLikes[position],postfavorite[position],listener)
        }else if (dataSet[position].type == PostType.VIDEO.typeId){
            (holder as PostVideoViewHolder).bind(dataSet[position])
        }
    }

    override fun getItemCount(): Int = dataSet.size

    class DiffCallback(
        private val oldDataSet: List<FeedPosts>,
        private val newDataSet: List<FeedPosts>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldDataSet.size
        override fun getNewListSize(): Int = newDataSet.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldDataSet[oldItemPosition].type == newDataSet[newItemPosition].type

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldDataSet[oldItemPosition] == newDataSet[newItemPosition]
    }

}
