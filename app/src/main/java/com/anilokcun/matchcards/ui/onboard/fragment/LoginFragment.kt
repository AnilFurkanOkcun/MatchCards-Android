package com.anilokcun.matchcards.ui.onboard.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.data.model.internal.api.Status
import com.anilokcun.matchcards.ui.base.fragment.BaseFragment
import com.anilokcun.matchcards.ui.onboard.viewmodel.OnboardViewModel
import com.anilokcun.matchcards.util.*
import com.anilokcun.matchcards.util.helper.buildMainIntent
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

	private val onboardViewModel by lazy { ViewModelProviders.of(this).get(OnboardViewModel::class.java) }
	private val navController by lazy { findNavController() }

	private val progressDialog by lazy { context?.getProgressDialog(R.string.progress_logging_in) }

	override fun getLayoutResId() = R.layout.fragment_login

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		// Set maxLengths to editTexts
		etNickname.setMaxLength(resources.getInteger(R.integer.nickname_max_char))
		etPassword.setMaxLength(resources.getInteger(R.integer.password_max_char))
		observeLoginResult()
		etNickname.focusAndShowKeyboard()
		setListeners()
	}

	private fun setListeners() {
		/* Nickname EditText'S TextChangedListener */
		etNickname.addTextChangedListenerWithUpdateFunction(::updateLoginButtonEnableStatus)
		/* Password EditText'S TextChangedListener */
		etPassword.addTextChangedListenerWithUpdateFunction(::updateLoginButtonEnableStatus)
		/* Login Button's ClickListener */
		btnLogin.setOnClickListener {
			onboardViewModel.login(etNickname.trimmedText(), etPassword.trimmedText())
		}
		/* Register Text's ClickListener*/
		tvRegister.setOnClickListener {
			navController.navigate(R.id.action_loginFragment_to_registerFragment)
		}
	}

	private fun observeLoginResult() {
		onboardViewModel.loginResult.observe(this, Observer { result ->
			if (result.status == Status.LOADING) {
				progressDialog?.show()
			} else {
				progressDialog?.dismiss()
			}
			when (result.status) {
				Status.SUCCESS -> {
					startActivity(context?.buildMainIntent())
					activity?.finish()
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

	private fun updateLoginButtonEnableStatus() {
		btnLogin.isEnabled = etNickname.trimmedText().isNotEmpty()
				&& etPassword.trimmedText().isNotEmpty()
	}
}
