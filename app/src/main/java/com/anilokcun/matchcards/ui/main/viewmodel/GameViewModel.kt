package com.anilokcun.matchcards.ui.main.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anilokcun.matchcards.data.model.dto.LeaderDTO
import com.anilokcun.matchcards.data.model.internal.api.EmptyResource
import com.anilokcun.matchcards.data.model.internal.game.Level
import com.anilokcun.matchcards.data.model.internal.user.UserData
import com.anilokcun.matchcards.data.repository.ApiRepository
import com.anilokcun.matchcards.data.repository.game.LevelRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class GameViewModel : ViewModel() {

	private val apiRepository by lazy { ApiRepository() }
	// Game
	private var gameTimeMs = 0L
	private var gameLevels = LevelRepository().getLevels()
	private var currentLevelIndex = 0
	private var noOfMatchesFound = 0
	private var countDownTimer: CountDownTimer? = null
	val currentGameLevel by lazy { MutableLiveData<Level?>() }
	val remainingTimeMs by lazy { MutableLiveData<Long>() }
	val score by lazy { MutableLiveData<Int>() }
	val saveUserScoreResult by lazy { MutableLiveData<EmptyResource>() }

	fun startGame(gameTimeMs: Long) {
		this.gameTimeMs = gameTimeMs
		currentGameLevel.value = gameLevels[currentLevelIndex]
		score.value = 0
		startCountDownTimer(gameTimeMs)
	}

	private fun startCountDownTimer(timeMs: Long) {
		countDownTimer = object : CountDownTimer(timeMs, 1000L) {
			override fun onTick(millisUntilFinished: Long) {
				remainingTimeMs.value = millisUntilFinished
			}

			override fun onFinish() {
				remainingTimeMs.value = 0
			}

		}.start()
	}

	fun matchFound() {
		val currentLevel = gameLevels[currentLevelIndex]
		// Update number of matches found
		noOfMatchesFound++
		// Update score
		val currentScore = score.value ?: 0
		val updatedScore = currentScore + currentLevel.matchPoint
		score.value = updatedScore
		// If  all cards of the current level found
		if (noOfMatchesFound == currentLevel.cardCount / 2) {
			// Get next level
			currentGameLevel.value =
				if (currentLevelIndex + 1 <= gameLevels.lastIndex) {
					// If there is a next level, get next level
					noOfMatchesFound = 0
					currentLevelIndex++
					gameLevels[currentLevelIndex]
				} else {
					// If all levels completed, update score with remaining time bonus
					val remainingSecs = ((remainingTimeMs.value ?: 0L) / 1000).toInt()
					score.value = updatedScore + (remainingSecs * 3)
					// And send null
					null
				}
		}
	}

	fun startTimer() {
		startCountDownTimer(remainingTimeMs.value ?: gameTimeMs)
	}

	fun pauseTimer() {
		countDownTimer?.cancel()
		countDownTimer = null
	}

	fun restartGame() {
		currentLevelIndex = 0
		noOfMatchesFound = 0
		countDownTimer?.cancel()
		countDownTimer = null
		gameLevels = LevelRepository().getLevels()
		startGame(gameTimeMs)
	}

	fun saveUserScore(score: Int) {
		saveScoreToRemoteDb(score)
	}

	private fun saveScoreToRemoteDb(score: Int) {
		saveUserScoreResult.value = EmptyResource.loading()
		val currentUser = apiRepository.getCurrentUser() ?: run {
			saveUserScoreResult.value = EmptyResource.error()
			return
		}
		val tasks = arrayListOf<Task<Void>>()

		// Save user's last score
		val taskSaveScoreToUserLastScore = apiRepository.saveScoreToUserLastScore(currentUser.uid, score)
		tasks.add(taskSaveScoreToUserLastScore)

		if (score > UserData.highScore) {
			// Save user's high score to users table
			val taskSaveScoreToUserHighScore = apiRepository.saveScoreToUserHighScore(currentUser.uid, score)
			tasks.add(taskSaveScoreToUserHighScore)

			// Save user to leaders table with high score
			val leaderDTO = LeaderDTO(UserData.nickname, UserData.avatarUrl, score)
			val taskSaveScoreToLeaders = apiRepository.saveLeader(currentUser.uid, leaderDTO)
			tasks.add(taskSaveScoreToLeaders)
		}


		Tasks.whenAll(tasks)
			.addOnSuccessListener {
				saveUserScoreResult.value = EmptyResource.success()
			}.addOnFailureListener {
				saveUserScoreResult.value = EmptyResource.error(it)
			}
	}

	override fun onCleared() {
		super.onCleared()
		countDownTimer?.cancel()
		countDownTimer = null
	}
}
