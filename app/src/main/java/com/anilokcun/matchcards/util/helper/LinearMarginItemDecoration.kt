package com.anilokcun.matchcards.util.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearMarginItemDecoration(
	@RecyclerView.Orientation private val orientation: Int, private val spacingPx: Int, private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {
	override fun getItemOffsets(
		outRect: Rect, view: View,
		parent: RecyclerView, state: RecyclerView.State
	) {
		with(outRect) {
			if (orientation == RecyclerView.VERTICAL) {
				if (includeEdge) {
					if (parent.getChildAdapterPosition(view) == 0) {
						top = spacingPx
					}
					bottom = spacingPx
				} else {
					if (parent.getChildAdapterPosition(view) != 0) {
						top = spacingPx
					}
				}
			} else if (orientation == RecyclerView.HORIZONTAL) {
				if (includeEdge) {
					if (parent.getChildAdapterPosition(view) == 0) {
						left = spacingPx
					}
					right = spacingPx
				} else {
					if (parent.getChildAdapterPosition(view) != 0) {
						left = spacingPx
					}
				}
			}
		}
	}
}