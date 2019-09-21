package com.anilokcun.matchcards.data.model.internal.api

data class Resource<out T> internal constructor(val status: Status, val data: T?, val exception: Exception?) {
	companion object {
		fun <T> success(data: T?) = Resource(Status.SUCCESS, data, null)

		fun <T> error(exception: Exception? = null) = Resource<T>(Status.ERROR, null, exception)

		fun <T> loading() = Resource<T>(Status.LOADING, null, null)
	}
}