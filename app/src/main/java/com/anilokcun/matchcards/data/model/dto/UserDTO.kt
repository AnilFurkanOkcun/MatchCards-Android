package com.anilokcun.matchcards.data.model.dto

data class UserDTO(
	val id: String = "",
	val nickname: String = "",
	val avatarUrl: String = "",
	val lastScore: Int = 0,
	val highScore: Int = 0
)