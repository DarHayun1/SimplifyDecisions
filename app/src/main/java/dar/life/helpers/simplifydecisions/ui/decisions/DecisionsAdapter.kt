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

class DecisionsAdapter(private val mContext: Context) :
    RecyclerView.Adapter<DecisionsAdapter.DecisionsVH>() {

    var mDecisions: List<Decision> = mutableListOf()

    fun setData(decisions: List<Decision>) {
        mDecisions = decisions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecisionsVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.decisions_list_item, parent, false)
        return DecisionsVH(view)
    }

    override fun getItemCount(): Int = mDecisions.size

    override fun onBindViewHolder(holder: DecisionsVH, position: Int) {
        holder.bindItem(mDecisions[position])
    }


    class DecisionsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.decision_item_title_tv)
        val description: TextView = itemView.findViewById(R.id.decision_item_desc_tv)


        fun bindItem(decision: Decision) {
            title.text = decision.title
            description.text = decision.description
        }


    }
}