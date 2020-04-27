package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews

class DecisionsAdapter(private val mContext: Context, private val mCallback: OnDetailsRequest) :
    RecyclerView.Adapter<DecisionsAdapter.DecisionsVH>() {

    var decisions: List<Decision> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var expandedPos = -1

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

    private fun launchDetailsScreen(decision: Decision, itemView: View) {
        mCallback.openDetailsScreen(decision.id, decision.title, itemView)
    }


    class DecisionsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.decision_item_title_tv)
        val description: TextView = itemView.findViewById(R.id.decision_item_desc_tv)
        val extraInfoLayout: View = itemView.findViewById(R.id.decision_item_extra_info)


        fun bindItem(decision: Decision) {
            title.text = decision.title
            description.text = decision.description
        }


    }
}