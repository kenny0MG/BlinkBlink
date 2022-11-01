package org.robotics.blinkblink.Activity.Chats

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


abstract class SwipeGesture(context:Context): ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT ) {
    val rightAnyColor = ContextCompat.getColor(context,R.color.holo_red_light)
    val leftAnyColor = ContextCompat.getColor(context,R.color.holo_blue_light)
    val labelColor = ContextCompat.getColor(context,R.color.white)
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        RecyclerViewSwipeDecorator.Builder(c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive)
            .addSwipeLeftBackgroundColor(rightAnyColor)
            .addSwipeLeftLabel("Delete").addCornerRadius(0,0).setSwipeLeftLabelColor(labelColor)
            .setSwipeLeftLabelTextSize(0, 45F).setSwipeLeftLabelTypeface(Typeface.SANS_SERIF).
            addSwipeRightBackgroundColor(leftAnyColor).
                addSwipeRightLabel("Clean chat").setSwipeRightLabelColor(labelColor)
            .setSwipeRightLabelTextSize(0, 45F).
                setSwipeRightLabelTypeface(Typeface.DEFAULT_BOLD)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
//or ItemTouchHelper.RIGHT