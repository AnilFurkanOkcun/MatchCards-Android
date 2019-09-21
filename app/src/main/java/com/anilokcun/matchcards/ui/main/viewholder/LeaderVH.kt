package com.anilokcun.matchcards.ui.main.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.data.model.dto.LeaderDTO
import com.anilokcun.matchcards.util.loadImageUrl
import kotlinx.android.synthetic.main.item_leader.view.*

class LeaderVH(parent: ViewGroup) :
	RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_leader, parent, false)) {

	fun bind(leader: LeaderDTO) {
		/* Leader's Order */
		itemView.tvOrder.text = (adapterPosition + 1 + 3).toString()
		/* Leader's Avatar */
		itemView.imgAvatar.loadImageUrl(leader.avatarUrl)
		/* Leader's Nickname */
		itemView.tvNickname.text = leader.nickname
		/* Leader's High Score */
		itemView.tvScore.text = leader.highScore.toString()
	}
}