package com.anilokcun.matchcards.ui.main.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.matchcards.R
import com.anilokcun.matchcards.data.model.internal.game.Card
import com.anilokcun.matchcards.util.CardWithPositionCallBack

class CardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

	fun bind(card: Card, isCardClickable: Boolean, clickListener: CardWithPositionCallBack) {
		if (itemView !is ImageView) return

		/* Card's Image Resource */
		val cardImageResource = if (card.isFlippedFront) card.drawableResId else R.drawable.img_card_back
		itemView.setImageResource(cardImageResource)

		/* Card's click functions*/
		if (!card.isFlippedFront && isCardClickable && !card.isMatchFound) {
			itemView.isClickable = true
			itemView.isEnabled = true
			itemView.setOnClickListener {
				clickListener.invoke(card, adapterPosition)
			}
		} else {
			itemView.isClickable = false
			itemView.isEnabled = false
		}

		/* is Card's match found */
		itemView.alpha = if (card.isMatchFound) 0.3f else 1f
	}
}
