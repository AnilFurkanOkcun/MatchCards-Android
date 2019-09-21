package com.anilokcun.matchcards.data.repository.game

import com.anilokcun.matchcards.R

class CardDrawableResRepository {

	private val cardDrawableResList = listOf(
		R.drawable.flag_argentina,
		R.drawable.flag_brazil,
		R.drawable.flag_canada,
		R.drawable.flag_china,
		R.drawable.flag_england,
		R.drawable.flag_france,
		R.drawable.flag_germany,
		R.drawable.flag_ireland,
		R.drawable.flag_italy,
		R.drawable.flag_japan,
		R.drawable.flag_russia,
		R.drawable.flag_south_korea,
		R.drawable.flag_spain,
		R.drawable.flag_turkey,
		R.drawable.flag_united_states
	)

	fun getRandomCardsDrawableRes(count: Int) = cardDrawableResList.shuffled().take(count)
}