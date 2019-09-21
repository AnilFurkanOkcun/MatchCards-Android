package com.anilokcun.matchcards.ui.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.matchcards.data.model.dto.LeaderDTO
import com.anilokcun.matchcards.ui.main.viewholder.LeaderVH

class LeadersRVAdapter(private val leadersList: List<LeaderDTO>) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LeaderVH(parent)

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is LeaderVH) {
			holder.bind(leadersList[position])
		}
	}

	override fun getItemCount() = leadersList.size
}
