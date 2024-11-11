package com.nafis.moneylaundry.fragments

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Menambahkan jarak hanya di sisi bawah setiap item, kecuali item terakhir
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position != itemCount - 1) {
            outRect.bottom = spaceHeight
        }
    }
}
