package com.anilokcun.matchcards.data.model.internal.game

import com.anilokcun.matchcards.data.repository.game.CardDrawableResRepository

data class Level(
	val level: Int,
	val cardCount: Int,
	val numberOfColumns: Int,
	val matchPoint: Int,
	var cards: List<Card> = listOf()
) {
	init {
		val randomCardsDrawableRes = CardDrawableResRepository().getRandomCardsDrawableRes(cardCount / 2)
		val cardList = arrayListOf<Card>()
		for (drawableRes in randomCardsDrawableRes) {
			cardList.add(Card(drawableRes))
			cardList.add(Card(drawableRes))
		}
		cardList.shuffle()
		cards = cardList.toList()
	}
}