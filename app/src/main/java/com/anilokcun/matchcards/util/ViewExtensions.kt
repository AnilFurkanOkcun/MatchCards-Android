package com.anilokcun.matchcards.util

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

/** Starts Click Alpha Animation for given view
 * @param fromAlpha Starting alpha value for the animation, where 1.0 means
 *        fully opaque and 0.0 means fully transparent.
 * @param toAlpha Ending alpha value for the animation.
 * @param duration Duration of the animation. */
fun View.startClickAlphaAnimation(fromAlpha: Float = 1f, toAlpha: Float = 0.7f, duration: Long = 50) {
	val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
	alphaAnimation.duration = duration
	this.startAnimation(alphaAnimation)
}

/** Extension for simplify create basic Toast Message */
fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT): Toast {
	return Toast.makeText(this, msg, duration).apply { show() }
}

/** Extension for simplify create basic Toast Message with String resource */
fun Context.toastStringRes(resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast {
	return Toast.makeText(this, this.getString(resId), duration).apply { show() }
}

/** Creates basic Snackbar Message */
fun View.snackbar(@StringRes messageStringRes: Int, duration: Int = Snackbar.LENGTH_LONG): Snackbar {
	return this.snackbar(this.resources.getString(messageStringRes), duration)
}

/** Creates basic Snackbar Message */
fun View.snackbar(msg: String, duration: Int = Snackbar.LENGTH_LONG): Snackbar {
	return Snackbar.make(this, msg, duration).apply { show() }
}

/** Sets an action for snackbar */
fun Snackbar.action(@StringRes actionTextStringRes: Int, actionTextColor: Int? = null, listener: (View) -> Unit) {
	action(view.resources.getString(actionTextStringRes), actionTextColor, listener)
}

/** Sets an action for snackbar */
fun Snackbar.action(actionText: String, actionTextColor: Int? = null, listener: (View) -> Unit) {
	setAction(actionText, listener)
	actionTextColor?.let { setActionTextColor(actionTextColor) }
}

/** Gives trimmed text of EditText */
fun EditText.trimmedText() = text.toString().trim()

/** Adds TextChangedListener for invoking given function when written text changed */
inline fun EditText.addTextChangedListenerWithUpdateFunction(crossinline updateFunction: () -> Unit) {
	addTextChangedListener(object : TextWatcher {
		override fun afterTextChanged(s: Editable?) {
			updateFunction.invoke()
		}

		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
	})
}

/** Sets max length for given EditText with InputFilters */
fun EditText.setMaxLength(length: Int) {
	val filtersArray = arrayListOf<InputFilter>()
	filtersArray.addAll(filters)
	filtersArray.add(InputFilter.LengthFilter(length))
	filters = filtersArray.toArray(arrayOf())
}

/** Shows keyboard, it does not matter which view it is */
fun View.showKeyboard() {
	(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
		.toggleSoftInputFromWindow(
			applicationWindowToken,
			InputMethodManager.SHOW_IMPLICIT, 0
		)
}

/** Focuses edittext and opens keyboard */
fun EditText.focusAndShowKeyboard() {
	clearFocus()
	requestFocus()
	Handler().postDelayed({
		showKeyboard()
	}, 25)
}