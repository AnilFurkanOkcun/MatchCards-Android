package com.anilokcun.matchcards.util.listeners

interface RequestPermissionResultListener {

	fun onPermissionGranted()

	fun onPermissionDenied()

	fun onPermissionPermanentlyDenied()
}