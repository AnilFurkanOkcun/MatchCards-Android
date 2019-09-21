package com.anilokcun.matchcards.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.anilokcun.matchcards.R
import kotlinx.android.synthetic.main.dialog_game.view.*
import kotlinx.android.synthetic.main.dialog_progress.view.*

/** @return simple progress dialog with text,
 * @param bodyResId String resource id for text of the dialog (Optional) */
fun Context.getProgressDialog(@StringRes bodyResId: Int?): AlertDialog {
	val text = if (bodyResId != null) getString(bodyResId) else null
	return this.getProgressDialog(text)
}

/** @return simple progress dialog with text,
 * @param body Body Text of the dialog (Optional) */
fun Context.getProgressDialog(body: String? = null): AlertDialog {
	val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)

	val tvBody = dialogView.tvBody

	// Dialog Body Text
	if (body != null && body.isNotEmpty()) {
		tvBody.text = body
	} else {
		tvBody.visibility = View.GONE
	}

	return AlertDialog.Builder(this)
		.setView(dialogView)
		.setCancelable(false)
		.create()
		.apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }
}

/** @return AlertDialog with title,3 info text and 2 buttons,
 * @param title Title of the dialog,
 * @param titleColorResId Color resource id of the Title,
 * @param info1 Text of the info1 TextView(Optional)
 * @param info2 Text of the info2 TextView(Optional)
 * @param info3 Text of the info3 TextView(Optional)
 * @param infoBackgroundColorResId Color resource id of the info texts' backgrounds,
 * @param btn1Text Text of the Button1,
 * @param btn1ClickListener Click callback of the Button1,
 * @param btn1Text Text of the Button2(Optional),
 * @param btn1ClickListener Click callback of the Button2(Optional),*/
fun Context.getGameDialog(
	title: String,
	@ColorRes titleColorResId: Int,
	info1: String? = null,
	info2: String? = null,
	info3: String? = null,
	@ColorRes infoBackgroundColorResId: Int,
	btn1Text: String,
	btn1ClickListener: () -> Unit,
	btn2Text: String? = null,
	btn2ClickListener: (() -> Unit)? = null
): AlertDialog {
	val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_game, null)
	val titleColor = ContextCompat.getColor(this, titleColorResId)
	val infoBackgroundColor = ContextCompat.getColor(this, infoBackgroundColorResId)
	// Dialog Title
	dialogView.tvTitle.text = title
	dialogView.tvTitle.setTextColor(titleColor)
	// Info 1
	if (info1 != null) {
		dialogView.tvInfo1.text = info1
		dialogView.tvInfo1.setBackgroundColor(infoBackgroundColor)
	} else {
		dialogView.tvInfo1.visibility = View.GONE
	}
	// Info 2
	if (info2 != null) {
		dialogView.tvInfo2.text = info2
		dialogView.tvInfo2.setBackgroundColor(infoBackgroundColor)
	} else {
		dialogView.tvInfo2.visibility = View.GONE
	}
	// Info 3
	if (info3 != null) {
		dialogView.tvInfo3.text = info3
		dialogView.tvInfo3.setBackgroundColor(infoBackgroundColor)
	} else {
		dialogView.tvInfo3.visibility = View.GONE
	}
	// Button 1
	dialogView.btn1.text = btn1Text
	dialogView.btn1.setOnClickListener {
		btn1ClickListener.invoke()
	}
	// Button 2
	if (btn2Text != null) {
		dialogView.btn2.text = btn2Text
		dialogView.btn2.setOnClickListener {
			btn2ClickListener?.invoke()
		}
	} else {
		dialogView.btn2.visibility = View.GONE
	}

	return AlertDialog.Builder(this)
		.setView(dialogView)
		.setCancelable(true)
		.create()
		.apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }
}