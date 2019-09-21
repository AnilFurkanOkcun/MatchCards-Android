package com.anilokcun.matchcards.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anilokcun.matchcards.data.model.dto.LeaderDTO
import com.anilokcun.matchcards.data.model.internal.api.Resource
import com.anilokcun.matchcards.data.repository.ApiRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeadersViewModel : ViewModel() {

	private val apiRepository by lazy { ApiRepository() }
	val leadersResult by lazy { MutableLiveData<Resource<List<LeaderDTO>>>() }

	fun getLeaders(leadersCountLimit: Int) {
		leadersResult.value = Resource.loading()
		apiRepository.getLeaders(leadersCountLimit)
			.addValueEventListener(object : ValueEventListener {
				override fun onDataChange(dataSnapshot: DataSnapshot) {
					val leaders = arrayListOf<LeaderDTO>()
					for (leaderDataSnapShot in dataSnapshot.children) {
						val leader = leaderDataSnapShot.getValue(LeaderDTO::class.java)
						leaders.add(leader ?: continue)
					}
					leaders.reverse()
					leadersResult.value = Resource.success(leaders)
				}

				override fun onCancelled(databaseError: DatabaseError) {
					leadersResult.value = Resource.error(databaseError.toException())
				}
			})
	}
}
