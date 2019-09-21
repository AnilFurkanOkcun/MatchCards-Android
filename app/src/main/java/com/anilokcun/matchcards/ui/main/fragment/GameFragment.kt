package com.anilokcun.matchcards.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.data.model.internal.api.Status
import com.anilokcun.matchcards.data.model.internal.game.Card
import com.anilokcun.matchcards.data.model.internal.game.Level
import com.anilokcun.matchcards.data.model.internal.user.UserData
import com.anilokcun.matchcards.ui.base.fragment.BaseFragment
import com.anilokcun.matchcards.ui.main.adapter.CardsRVAdapter
import com.anilokcun.matchcards.ui.main.viewmodel.GameViewModel
import com.anilokcun.matchcards.util.getGameDialog
import com.anilokcun.matchcards.util.helper.GridSpacingItemDecoration
import com.anilokcun.matchcards.util.toastStringRes
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : BaseFragment() {

	private val gameViewModel by lazy { ViewModelProviders.of(this).get(GameViewModel::class.java) }

	private lateinit var cardsRvAdapter: CardsRVAdapter

	private var firstFlippedCardWithPosition: Pair<Int, Card>? = null
	private var secondFlippedCardWithPosition: Pair<Int, Card>? = null

	private val gameTimeMs by lazy { resources.getInteger(R.integer.game_time_ms).toLong() }
	private val flippedCardsNoMatchDurationTimeMs by lazy {
		resources.getInteger(R.integer.flipped_cards_no_match_duration_time_ms).toLong()
	}
	private val flippedCardsMatchDurationTimeMs by lazy {
		resources.getInteger(R.integer.flipped_cards_match_duration_time_ms).toLong()
	}

	override fun getLayoutResId() = R.layout.fragment_game

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observeGameLevel()
		observeRemainingTime()
		observeScore()
		observeSaveUserScoreResult()
		gameViewModel.startGame(gameTimeMs)
	}

	private fun observeGameLevel() {
		gameViewModel.currentGameLevel.observe(this, Observer { level ->
			if (level == null) {
				gameSuccessfullyFinished(
					score = gameViewModel.score.value ?: 0,
					remainingTimeMs = gameViewModel.remainingTimeMs.value ?: 0L
				)
				return@Observer
			}
			if (level.level == 1) {
				startLevel(level)
			} else {
				levelFinished(
					nextLevel = level,
					score = gameViewModel.score.value ?: 0,
					remainingTimeMs = gameViewModel.remainingTimeMs.value ?: 0L
				)
			}
		})
	}

	private fun observeRemainingTime() {
		gameViewModel.remainingTimeMs.observe(this, Observer { remainingTimeMs ->
			val remainingTimeMinSec = getTimeMinSec(remainingTimeMs)
			tvTime.text = getString(R.string.game_time, remainingTimeMinSec.first, remainingTimeMinSec.second)
			if (remainingTimeMs == 0L) {
				// Time is over
				gameOver(score = gameViewModel.score.value ?: 0)
			}
		})
	}

	private fun observeScore() {
		gameViewModel.score.observe(this, Observer { score ->
			tvScore.text = getString(R.string.game_score, score)
		})
	}

	private fun observeSaveUserScoreResult() {
		gameViewModel.saveUserScoreResult.observe(this, Observer { result ->
			when (result.status) {
				Status.SUCCESS -> {
					context?.toastStringRes(R.string.toast_your_score_has_been_saved)
				}
				Status.ERROR -> {
					context?.toastStringRes(R.string.toast_your_score_couldnt_save)
				}
				Status.LOADING -> {
				}
			}
		})
	}

	private fun startLevel(level: Level) {
		setCardsRecyclerView(level.cards, level.numberOfColumns)
	}

	private fun levelFinished(nextLevel: Level, score: Int, remainingTimeMs: Long) {
		gameViewModel.pauseTimer()
		var levelFinishedDialog: AlertDialog? = null
		val oldLevel = nextLevel.level - 1
		val remainingTimeMinSec = getTimeMinSec(remainingTimeMs)
		levelFinishedDialog = context?.getGameDialog(
			title = getString(R.string.game_dialog_title_level_completed, oldLevel),
			titleColorResId = R.color.blue800,
			info1 = getString(R.string.game_score, score),
			info2 = getString(
				R.string.game_dialog_info_remaining_time,
				remainingTimeMinSec.first,
				remainingTimeMinSec.second
			),
			infoBackgroundColorResId = R.color.blue400,
			btn1Text = getString(R.string.game_dialog_btn_next_level),
			btn1ClickListener = {
				levelFinishedDialog?.dismiss()
				goNextLevel(nextLevel)
			}
		)?.apply {
			setCancelable(false)
			show()
		}
	}

	private fun goNextLevel(nextLevel: Level) {
		gameViewModel.startTimer()
		startLevel(nextLevel)
	}

	private fun gameSuccessfullyFinished(score: Int, remainingTimeMs: Long) {
		gameViewModel.pauseTimer()
		gameViewModel.saveUserScore(score)

		var gameFinishedDialog: AlertDialog? = null
		val remainingTimeMinSec = getTimeMinSec(remainingTimeMs)
		gameFinishedDialog = context?.getGameDialog(
			title = getString(R.string.game_dialog_title_congrats),
			titleColorResId = R.color.green400,
			info1 = getString(R.string.game_score, score),
			info2 = getString(
				R.string.game_dialog_info_remaining_time,
				remainingTimeMinSec.first,
				remainingTimeMinSec.second
			),
			info3 = getString(R.string.game_dialog_info_your_last_high_score, UserData.highScore),
			infoBackgroundColorResId = R.color.green800,
			btn1Text = getString(R.string.game_dialog_btn_play_again),
			btn1ClickListener = {
				gameFinishedDialog?.dismiss()
				playAgain()
			},
			btn2Text = getString(R.string.game_dialog_btn_share_your_score),
			btn2ClickListener = { shareScore(score) }
		)?.apply {
			setOnCancelListener {
				activity?.onBackPressed()
			}
			show()
		}
	}

	private fun gameOver(score: Int) {
		gameViewModel.pauseTimer()
		gameViewModel.saveUserScore(score)

		var gameOverDialog: AlertDialog? = null
		gameOverDialog = context?.getGameDialog(
			title = getString(R.string.game_dialog_title_game_over),
			titleColorResId = R.color.red400,
			info1 = getString(R.string.game_score, score),
			info2 = getString(R.string.game_dialog_info_your_last_high_score, UserData.highScore),
			infoBackgroundColorResId = R.color.red800,
			btn1Text = getString(R.string.game_dialog_btn_play_again),
			btn1ClickListener = {
				gameOverDialog?.dismiss()
				playAgain()
			},
			btn2Text = getString(R.string.game_dialog_btn_share_your_score),
			btn2ClickListener = { shareScore(score) }
		)?.apply {
			setOnCancelListener {
				activity?.onBackPressed()
			}
			show()
		}
	}

	private fun playAgain() {
		gameViewModel.restartGame()
	}

	private fun shareScore(score: Int) {
		val appName = getString(R.string.app_name)
		val appUrl = getString(R.string.app_url)
		val sharingText = getString(R.string.score_sharing_text, score, appName, appUrl)
		val shareIntent = Intent()
		shareIntent.action = Intent.ACTION_SEND
		shareIntent.putExtra(Intent.EXTRA_TEXT, sharingText)
		shareIntent.type = "text/plain"
		startActivity(shareIntent)

	}

	private fun setCardsRecyclerView(cards: List<Card>, numberOfColumns: Int) {
		// Calculate cardSizePx
		val rvWidthPx = resources.displayMetrics.widthPixels
		val gameCardsSpacingPx = if (numberOfColumns > 3) {
			resources.getDimensionPixelSize(R.dimen.game_cards_small_spacing)
		} else {
			resources.getDimensionPixelSize(R.dimen.game_cards_large_spacing)
		}

		val totalSpacingWidthPx = ((numberOfColumns + 1) * gameCardsSpacingPx)
		val cardSizePx = (rvWidthPx - totalSpacingWidthPx) / numberOfColumns
		// Set recyclerView
		rvCards.layoutManager = GridLayoutManager(context, numberOfColumns)
		if (rvCards.itemDecorationCount > 0) {
			rvCards.removeItemDecorationAt(0)
		}
		rvCards.addItemDecoration(GridSpacingItemDecoration(numberOfColumns, gameCardsSpacingPx, true))
		cardsRvAdapter = CardsRVAdapter(cards, cardSizePx, ::onCardClick)
		rvCards.adapter = cardsRvAdapter
	}

	private fun onCardClick(card: Card, cardPosition: Int) {
		if (firstFlippedCardWithPosition != null && secondFlippedCardWithPosition != null) return
		// If the card is facing backwards
		if (!card.isFlippedFront) {
			cardsRvAdapter.flipCard(cardPosition)
			if (firstFlippedCardWithPosition == null) {
				firstFlippedCardWithPosition = Pair(cardPosition, card)
			} else {
				secondFlippedCardWithPosition = Pair(cardPosition, card)
				checkCardsForMatch()
			}
		}
	}

	private fun checkCardsForMatch() {
		cardsRvAdapter.disableCardsClicks()
		val firstCardWithPosition = firstFlippedCardWithPosition ?: return
		val secondCardWithPosition = secondFlippedCardWithPosition ?: return
		val firstCard = firstCardWithPosition.second
		val firstCardPosition = firstCardWithPosition.first
		val secondCard = secondCardWithPosition.second
		val secondCardPosition = secondCardWithPosition.first

		if (firstCard.drawableResId == secondCard.drawableResId) {
			// Cards matched
			Handler().postDelayed({
				cardsRvAdapter.markCardsAsMatchFound(firstCardPosition, secondCardPosition)
				firstFlippedCardWithPosition = null
				secondFlippedCardWithPosition = null
				cardsRvAdapter.enableCardsClicks()
				gameViewModel.matchFound()
			}, flippedCardsMatchDurationTimeMs)
		} else {
			// No match
			Handler().postDelayed({
				cardsRvAdapter.flipCard(firstCardPosition)
				cardsRvAdapter.flipCard(secondCardPosition)
				firstFlippedCardWithPosition = null
				secondFlippedCardWithPosition = null
				cardsRvAdapter.enableCardsClicks()
			}, flippedCardsNoMatchDurationTimeMs)
		}
	}

	private fun getTimeMinSec(timeMs: Long): Pair<Int, Int> {
		val sec = (timeMs / 1000)
		val min = (sec / 60)
		return Pair(min.toInt(), (sec % 60).toInt())
	}

}


