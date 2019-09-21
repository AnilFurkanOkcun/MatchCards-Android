package com.anilokcun.matchcards.ui.onboard.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.data.model.internal.api.Status
import com.anilokcun.matchcards.ui.base.fragment.BaseFragment
import com.anilokcun.matchcards.ui.onboard.adapter.SelectAvatarRVAdapter
import com.anilokcun.matchcards.ui.onboard.viewmodel.OnboardViewModel
import com.anilokcun.matchcards.util.*
import com.anilokcun.matchcards.util.helper.GridSpacingItemDecoration
import com.anilokcun.matchcards.util.helper.buildMainIntent
import com.anilokcun.uwmediapicker.UwMediaPicker
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseFragment() {

	private val onboardViewModel by lazy { ViewModelProviders.of(this).get(OnboardViewModel::class.java) }
	private val requestCodePickImageForAvatar = 100

	private val signingUpProgressDialog by lazy { context?.getProgressDialog(R.string.progress_signing_up) }

	override fun getLayoutResId() = R.layout.fragment_register

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		// Set maxLengths to editTexts
		etNickname.setMaxLength(resources.getInteger(R.integer.nickname_max_char))
		etPassword.setMaxLength(resources.getInteger(R.integer.password_max_char))
		// Set random avatar image
		val randomAvatarDrawableRes = onboardViewModel.avatarRepository.getRandomAvatar()
		val randomAvatarBitmap = BitmapFactory.decodeResource(resources, randomAvatarDrawableRes)
		updateAvatarImage(randomAvatarBitmap)
		observeSignUpResult()
		setListeners()
	}

	private fun setListeners() {
		// Back Icon Image's ClickListener
		imgToolbarBack.setOnClickListener {
			activity?.onBackPressed()
		}
		// Avatar Layout's ClickListener
		frameAvatar.setOnClickListener {
			openSelectAvatarDialog()
		}
		/* Nickname EditText'S TextChangedListener */
		etNickname.addTextChangedListenerWithUpdateFunction(::updateSignupButtonEnableStatus)
		/* Password EditText'S TextChangedListener */
		etPassword.addTextChangedListenerWithUpdateFunction(::updateSignupButtonEnableStatus)
		/* Password 2 EditText'S TextChangedListener */
		etPassword2.addTextChangedListenerWithUpdateFunction(::updateSignupButtonEnableStatus)
		// Signup Button's ClickListener
		btnSignup.setOnClickListener {
			onboardViewModel.signUp(etNickname.trimmedText(), etPassword.trimmedText())
		}
	}

	private fun openSelectAvatarDialog() {
		val rvAvatars = LayoutInflater.from(context).inflate(R.layout.dialog_select_avatar, null) as? RecyclerView
			?: return

		val dialog = AlertDialog.Builder(context ?: return)
			.setView(rvAvatars)
			.create()
			.apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }

		val numberOfColumns = 3
		val gridSpacing = resources.getDimensionPixelSize(R.dimen.spacing_16)
		rvAvatars.layoutManager = GridLayoutManager(context, numberOfColumns)
		rvAvatars.addItemDecoration(GridSpacingItemDecoration(numberOfColumns, gridSpacing, false))
		rvAvatars.adapter =
			SelectAvatarRVAdapter(onboardViewModel.avatarRepository.getAvatarList()) { avatarDrawableRes ->
				dialog.dismiss()
				// On avatar clicked
				if (avatarDrawableRes != 0) {
					// Default avatar selected
					val avatarBitmap = BitmapFactory.decodeResource(resources, avatarDrawableRes)
					updateAvatarImage(avatarBitmap)
				} else {
					// Select avatar from image library
					requestToOpenGallery(requestCodePickImageForAvatar, ::openImageLibraryToSelectAvatar)
				}
			}
		dialog.show()
	}

	private fun openImageLibraryToSelectAvatar() {
		UwMediaPicker.with(this)
			.setRequestCode(requestCodePickImageForAvatar)
			.setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery)
			.setGridColumnCount(3)
			.setLightStatusBar(true)
			.setMaxSelectableMediaCount(1)
			.enableImageCompression(true)
			.setCompressionMaxWidth(resources.getInteger(R.integer.avatar_image_max_size).toFloat())
			.setCompressionMaxHeight(resources.getInteger(R.integer.avatar_image_max_size).toFloat())
			.setCompressedFileDestinationPath(context.getPrivateImagesFolderPath())
			.open()
	}

	private fun onAvatarSelectFailed() {
		context?.toast(getString(R.string.toast_error_image_select))
	}

	private fun updateAvatarImage(newAvatarBitmap: Bitmap?) {
		onboardViewModel.selectedAvatarBitmap = newAvatarBitmap
		imgAvatar.setImageBitmap(onboardViewModel.selectedAvatarBitmap)
	}

	private fun updateSignupButtonEnableStatus() {
		val passwordMinChar = resources.getInteger(R.integer.password_min_char)
		btnSignup.isEnabled = etNickname.trimmedText().isNotEmpty()
				&& etPassword.trimmedText().length >= passwordMinChar
				&& etPassword.text.toString() == etPassword2.text.toString()
	}

	private fun observeSignUpResult() {
		onboardViewModel.signUpResult.observe(this, Observer { result ->
			if (result.status == Status.LOADING) {
				signingUpProgressDialog?.show()
			} else {
				signingUpProgressDialog?.dismiss()
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

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<String>,
		grantResults: IntArray
	) {
		when (requestCode) {
			requestCodePickImageForAvatar -> {
				handleOpenGalleryPermissionResult(grantResults, btnSignup, ::openImageLibraryToSelectAvatar)
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (context == null || data == null || resultCode != Activity.RESULT_OK) return
		when (requestCode) {
			requestCodePickImageForAvatar -> {
				val selectedMediaPaths = data.getStringArrayExtra(UwMediaPicker.UwMediaPickerResultKey)
				if (selectedMediaPaths.isNotEmpty()) {
					// Get selected image
					val selectedAvatarImagePath = selectedMediaPaths[0]
					// Get selected image bitmap from file and make it square
					val selectedAvatarImageBitmap =
						BitmapFactory.decodeFile(selectedAvatarImagePath).changeAspectRatio(1f)
					updateAvatarImage(selectedAvatarImageBitmap)
				} else {
					// Error with Image Selecting
					onAvatarSelectFailed()
				}
			}
		}
	}
}
