package com.anilokcun.matchcards.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.data.model.dto.UserDTO
import com.anilokcun.matchcards.data.model.internal.api.Resource
import com.anilokcun.matchcards.data.model.internal.api.Status
import com.anilokcun.matchcards.data.model.internal.user.UserData
import com.anilokcun.matchcards.ui.base.fragment.BaseFragment
import com.anilokcun.matchcards.ui.splash.viewmodel.UserInfoViewModel
import com.anilokcun.matchcards.util.helper.buildOnboardIntent
import com.anilokcun.matchcards.util.loadImageUrl
import com.anilokcun.matchcards.util.toast
import com.anilokcun.matchcards.util.toastStringRes
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

	private val userInfoViewModel by lazy { ViewModelProviders.of(this).get(UserInfoViewModel::class.java) }
	private val navController by lazy { findNavController() }
	private lateinit var userInfoObserver: Observer<Resource<UserDTO>>

	override fun getLayoutResId() = R.layout.fragment_home

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setProfileInfo()
		observeUserInfo()
		userInfoViewModel.getUserInfo()
		setListeners()
	}

	private fun setListeners() {
		// LogOut Icon's ClickListener
		imgLogOut.setOnClickListener {
			userInfoViewModel.userInfoResult.removeObservers(this)
			userInfoViewModel.signOut()
			startActivity(context?.buildOnboardIntent())
			activity?.finish()
		}

		// Play Button's ClickListener
		btnPlay.setOnClickListener {
			navController.navigate(R.id.action_homeFragment_to_gameFragment)
		}

		// Leaders Button's ClickListener
		btnLeaders.setOnClickListener {
			navController.navigate(R.id.action_homeFragment_to_leadersFragment)
		}
	}

	private fun observeUserInfo() {
		userInfoViewModel.userInfoResult.observe(this, Observer { result ->
			when (result.status) {
				Status.SUCCESS -> {
					// Save user info
					val userDto = result.data ?: run {
						context?.toastStringRes(R.string.an_error_occurred)
						return@Observer
					}
					UserData.nickname = userDto.nickname
					UserData.avatarUrl = userDto.avatarUrl
					UserData.lastScore = userDto.lastScore
					UserData.highScore = userDto.highScore
					// Update profile area
					setProfileInfo()
				}
				Status.ERROR -> {
					if (result.exception != null) {
						context?.toast(result.exception.localizedMessage)
					} else {
						context?.toastStringRes(R.string.an_error_occurred)
					}
				}
				Status.LOADING -> {
				}
			}
		})
	}

	private fun setProfileInfo() {
		imgAvatar.loadImageUrl(UserData.avatarUrl)
		tvNickname.text = UserData.nickname
		tvScores.text = getString(R.string.home_user_scores, UserData.lastScore, UserData.highScore)
	}

}
