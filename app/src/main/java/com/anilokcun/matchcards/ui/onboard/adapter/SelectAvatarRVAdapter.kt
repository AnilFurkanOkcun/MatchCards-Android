package com.anilokcun.matchcards.ui.onboard.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.ui.onboard.viewholder.AvatarVH
import com.anilokcun.matchcards.util.AvatarDrawableResCallBack
import com.makeramen.roundedimageview.RoundedImageView

class SelectAvatarRVAdapter(private val avatarList: List<Int>, private val clickListener: AvatarDrawableResCallBack) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

		val imgAvatar = RoundedImageView(parent.context).apply {
			val avatarSizePx = resources.getDimensionPixelSize(R.dimen.select_avatar_dialog_avatar_size)
			layoutParams = ViewGroup.LayoutParams(avatarSizePx, avatarSizePx)
			borderWidth = resources.getDimensionPixelSize(R.dimen.avatar_stroke).toFloat()
			borderColor = ContextCompat.getColor(context, R.color.blue700)
			isOval = true
			isClickable = true
		}

		return AvatarVH(imgAvatar)
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is AvatarVH) holder.bind(avatarList[position], clickListener)
	}

	override fun getItemCount() = avatarList.size
}
