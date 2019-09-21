package com.anilokcun.matchcards.ui.onboard.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anilokcun.matchcards.data.model.dto.UserDTO
import com.anilokcun.matchcards.data.model.internal.api.EmptyResource
import com.anilokcun.matchcards.data.model.internal.user.UserData
import com.anilokcun.matchcards.data.repository.ApiRepository
import com.anilokcun.matchcards.data.repository.user.AvatarRepository
import com.anilokcun.matchcards.util.logError
import java.io.ByteArrayOutputStream

class OnboardViewModel : ViewModel() {

	private val apiRepository by lazy { ApiRepository() }

	val avatarRepository by lazy { AvatarRepository() }
	var selectedAvatarBitmap: Bitmap? = null

	val loginResult by lazy { MutableLiveData<EmptyResource>() }
	val signUpResult by lazy { MutableLiveData<EmptyResource>() }

	fun login(nickname: String, password: String) {
		loginResult.value = EmptyResource.loading()
		apiRepository.signInWithNicknameAndPassword(nickname, password)
			.addOnSuccessListener {
				UserData.nickname = nickname
				loginResult.value = EmptyResource.success()
			}
			.addOnFailureListener {
				loginResult.value = EmptyResource.error(it)
			}
	}

	fun signUp(nickname: String, password: String) {
		signUpResult.value = EmptyResource.loading()
		apiRepository.createUserWithNicknameAndPassword(nickname, password)
			.addOnSuccessListener {
				selectedAvatarBitmap?.let {
					uploadAvatarImageToRemoteStorage(nickname, it)
				} ?: run {
					signUpResult.value = EmptyResource.error()
				}
			}
			.addOnFailureListener {
				onSignupError(it)
			}
	}

	private fun uploadAvatarImageToRemoteStorage(nickname: String, avatarBitmap: Bitmap) {
		val currentUser = apiRepository.getCurrentUser() ?: run {
			onSignupError()
			return
		}

		val baos = ByteArrayOutputStream()
		avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
		val data = baos.toByteArray()

		apiRepository.putBytesToCurrentUserAvatarImage(currentUser.uid, data)
			.addOnSuccessListener {
				it.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadableUri ->
					saveUserToRemoteDb(nickname, downloadableUri.toString())
				}?.addOnFailureListener { exception ->
					onSignupError(exception)
				} ?: run {
					onSignupError()
				}
			}.addOnFailureListener {
				onSignupError(it)
			}
	}

	private fun saveUserToRemoteDb(nickname: String, avatarUrl: String) {
		val currentUser = apiRepository.getCurrentUser() ?: run {
			onSignupError()
			return
		}
		val userDto = UserDTO(currentUser.uid, nickname, avatarUrl)

		apiRepository.saveUser(userDto)
			.addOnSuccessListener {
				UserData.nickname = nickname
				UserData.avatarUrl = avatarUrl
				signUpResult.value = EmptyResource.success()
			}
			.addOnFailureListener {
				onSignupError(it)
			}
	}

	private fun onSignupError(exception: Exception? = null) {
		"Signup Error: $exception".logError()
		apiRepository.signOut()
		signUpResult.value = EmptyResource.error(exception)
	}
}
