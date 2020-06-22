package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.DecisionModel
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DecisionsAdapter(private val mContext: Context, private val mCallback: OnDetailsRequest) :
    RecyclerView.Adapter<DecisionsAdapter.DecisionsVH>() {

    var decisions: List<DecisionModel> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var expandedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecisionsVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.decisions_list_item, parent, false)
        return DecisionsVH(view)
    }

    override fun getItemCount(): Int = decisions.size

    override fun onBindViewHolder(holder: DecisionsVH, position: Int) {
        val decision = decisions[position]
        holder.bindItem(decisions[position])

        if (decision.expanded) {
            expandedPos = position
            fadeInViews(holder.extraInfoLayout)
        } else
            fadeOutViews(holder.extraInfoLayout)

        holder.itemView.setOnClickListener{
            if (decision.expanded) {
                expandedPos = -1
                decision.expanded = false
                launchDetailsScreen(decision, holder.title)
            } else {
                if (expandedPos != -1) {
                    decisions[expandedPos].expanded = false
                    notifyItemChanged(expandedPos)
                }
                decision.expanded = true
                notifyItemChanged(position)
            }
        }
    }

    private fun launchDetailsScreen(decision: DecisionModel, itemView: View) {
        mCallback.openDetailsScreen(decision.id, decision.title, itemView)
    }


    class DecisionsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.decision_item_title_tv)
        val description: TextView = itemView.findViewById(R.id.decision_item_desc_tv)
        val extraInfoLayout: View = itemView.findViewById(R.id.decision_item_extra_info)
        val decisionDate: TextView = itemView.findViewById(R.id.decision_item_date_tv)


        fun bindItem(decision: DecisionModel) {
            title.text = decision.title
            if (decision.description == null || decision.description!!.isEmpty())
                description.visibility = GONE
            else
                description.text = decision.description
            decisionDate.text =
                decision.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
            title.transitionName = decision.id.toString()
        }


    }
}