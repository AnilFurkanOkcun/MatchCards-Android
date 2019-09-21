package com.anilokcun.matchcards.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.util.listeners.RequestPermissionResultListener

/** Handles request permission result with given RequestPermissionResultListener */
fun Activity.handlePermissionResult(
	grantResults: IntArray,
	requestPermissionResultListener: RequestPermissionResultListener
) {
	// If request is cancelled, the result arrays are empty.
	if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
		// Permission granted
		requestPermissionResultListener.onPermissionGranted()
	} else {
		// Permission denied
		if (ActivityCompat.shouldShowRequestPermissionRationale(
				this,
				Manifest.permission.READ_EXTERNAL_STORAGE
			)
		) {
			// "Never Ask Again" is not checked
			requestPermissionResultListener.onPermissionDenied()
		} else {
			// "Never Ask Again" is checked
			requestPermissionResultListener.onPermissionPermanentlyDenied()
		}
	}
}

/** Handles request permission result, */
fun Activity.handlePermissionResultWithSnackbar(
	grantResults: IntArray, view: View?,
	@StringRes msgPermissionDeniedRes: Int,
	@StringRes msgPermissionDeniedPermanentlyRes: Int,
	permissionGrantedFunc: () -> Unit
) {
	this.handlePermissionResult(grantResults, object : RequestPermissionResultListener {

		override fun onPermissionGranted() {
			permissionGrantedFunc.invoke()
		}

		override fun onPermissionDenied() {
			view?.snackbar(msgPermissionDeniedRes)
		}

		override fun onPermissionPermanentlyDenied() {
			view?.snackbar(msgPermissionDeniedPermanentlyRes)
				?.action(R.string.settings) {
					// Opens application's settings page, for granting permission
					val intent = Intent()
					intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
					val uri = Uri.fromParts("package", packageName, null)
					intent.data = uri
					startActivity(intent)
				}
		}
	})
}

fun Fragment.handleOpenGalleryPermissionResult(grantResults: IntArray, view: View?, openGalleryFunc: () -> Unit) {
	activity?.handlePermissionResultWithSnackbar(
		grantResults, view, R.string.snackbar_msg_gallery_permission_denied,
		R.string.snackbar_msg_gallery_permission_denied_permanently, openGalleryFunc
	)
}

/** Requests for given permission and handles the run time permissions to invoke given requiredFunction */
fun Fragment.invokeFunctionWithPermission(permission: String, requestCode: Int, requiredFunction: () -> Unit) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
		&& ContextCompat.checkSelfPermission(context!!, permission)
		!= PackageManager.PERMISSION_GRANTED
	) {
		// Permission is not granted
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
			// Not first time (Already denied at least one time)
			requestPermissions(arrayOf(permission), requestCode)
		} else {
			// First time
			requestPermissions(arrayOf(permission), requestCode)
		}
	} else {
		// Permission has already been granted
		requiredFunction.invoke()
	}
}

/** Requests for READ_EXTERNAL_STORAGE permission and handles the run time permissions to invoke given requiredFunction */
fun Fragment.requestToOpenGallery(requestCode: Int, openGalleryFunction: () -> Unit) {
	invokeFunctionWithPermission(Manifest.permission.READ_EXTERNAL_STORAGE, requestCode, openGalleryFunction)
}


