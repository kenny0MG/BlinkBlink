package org.robotics.blinkblink.commons

import androidx.recyclerview.widget.DiffUtil
import org.robotics.blinkblink.models.CommonModel

class DiffUtilCalback(
    private val oldList: List<CommonModel>,
    private val newList: List<CommonModel>
): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].timeStamp == newList[newItemPosition].timeStamp

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}