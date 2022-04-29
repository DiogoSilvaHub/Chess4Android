package pt.isel.pdm.chess4android.history

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.lichessApi.ChallengeDTO


class HistoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view){

    private val resources = itemView.context.resources
    private val dayView = itemView.findViewById<TextView>(R.id.date)
    private val resolved = itemView.findViewById<TextView>(R.id.resolved)

    fun bindTo(challengeDTO: ChallengeDTO, onItemClick: () -> Unit) {
        dayView.text = challengeDTO.date
        resolved.text = if (challengeDTO.resolved) resources.getText(R.string.solved) else resources.getText(R.string.unsolved)


        itemView.setOnClickListener {
            itemView.isClickable = false
            startAnimation {
                onItemClick()
                itemView.isClickable = true
            }
        }

    }

    /**
     * Starts the item selection animation and calls [onAnimationEnd] once the animation ends
     */
    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(itemView.context, R.color.list_item_background),
            ContextCompat.getColor(itemView.context, R.color.list_item_background_selected),
            ContextCompat.getColor(itemView.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = itemView.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.doOnEnd { onAnimationEnd() }

        animation.start()
    }

}

class HistoryAdapter(
    private val dataSource: List<ChallengeDTO>,
    private val onItemClick: (ChallengeDTO) -> Unit
): RecyclerView.Adapter<HistoryItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_view, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bindTo(dataSource[position]) {
            onItemClick(dataSource[position])
        }
    }

    override fun getItemCount() = dataSource.size
}