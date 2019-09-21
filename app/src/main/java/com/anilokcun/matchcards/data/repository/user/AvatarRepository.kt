package com.anilokcun.matchcards.data.repository.user

import com.anilokcun.matchcards.R

class AvatarRepository {

	private val avatarList = listOf(
		R.drawable.avatar_1,
		R.drawable.avatar_2,
		R.drawable.avatar_3,
		R.drawable.avatar_4,
		R.drawable.avatar_5,
		R.drawable.avatar_6,
		R.drawable.avatar_7,
		R.drawable.avatar_8,
		0
	)

	fun getAvatarList() = avatarList

	fun getRandomAvatar() = avatarList.subList(0, avatarList.lastIndex).random()
}