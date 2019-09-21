package com.anilokcun.matchcards.util.helper

import android.content.Context
import android.content.Intent
import com.anilokcun.matchcards.ui.main.activity.MainActivity
import com.anilokcun.matchcards.ui.onboard.activity.OnboardActivity

fun Context.buildOnboardIntent(): Intent {
	return Intent(this, OnboardActivity::class.java).apply {
		flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
	}
}

fun Context.buildMainIntent(): Intent {
	return Intent(this, MainActivity::class.java).apply {
		flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
	}
}