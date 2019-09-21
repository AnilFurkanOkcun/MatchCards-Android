package com.anilokcun.matchcards.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.anilokcun.matchcards.BuildConfig
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.util.helper.GlideApp

/** Logs Info */
fun String?.logInfo(tag: String = "MatchCardsLog") {
	if (BuildConfig.DEBUG) {
		Log.i(tag, this.toString())
	}
}

/** Logs Error */
fun String?.logError(tag: String = "MatchCardsLog") {
	if (BuildConfig.DEBUG) {
		Log.e(tag, this.toString())
	}
}

/** Logs Debug */
fun String?.logDebug(tag: String = "MatchCardsLog") {
	if (BuildConfig.DEBUG) {
		Log.d(tag, this.toString())
	}
}

/** Logs Wtf */
fun String?.logWtf(tag: String = "MatchCardsLog") {
	if (BuildConfig.DEBUG) {
		Log.wtf(tag, this.toString())
	}
}

/** Gives private images folder path */
fun Context?.getPrivateImagesFolderPath(): String {
	return this?.getDir("Images", Context.MODE_PRIVATE)?.absolutePath ?: ""
}

/** Changes aspect ratio of the bitmap */
fun Bitmap.changeAspectRatio(newAspectRatio: Float): Bitmap? {
	val oldAspectRatio = width / height.toFloat()
	return if (oldAspectRatio != newAspectRatio) {
		if (oldAspectRatio > newAspectRatio) {
			val newWidth = (height * newAspectRatio).toInt()
			val trimWidth = width - newWidth
			Bitmap.createBitmap(this, trimWidth / 2, 0, newWidth, height)
		} else {
			val newHeight = (width / newAspectRatio).toInt()
			val trimHeight = height - newHeight
			Bitmap.createBitmap(this, 0, trimHeight / 2, width, newHeight)
		}
	} else this
}

/** Loads image in [imageUrl] to [ImageView] with Glide */
fun ImageView.loadImageUrl(imageUrl: String?, fallbackDrawableId: Int? = null) {
	GlideApp.with(this)
		.load(imageUrl)
		.placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorImagePlaceHolder)))
		.apply {
			fallbackDrawableId?.let {
				fallback(ContextCompat.getDrawable(context, fallbackDrawableId))
			}
		}
		.into(this)
}