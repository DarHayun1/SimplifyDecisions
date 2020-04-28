package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import dar.life.helpers.simplifydecisions.ui.UiUtils
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews

class IssuesAdapter(private val mContext: Context, private val mCallback: OnDetailsRequest) :
    RecyclerView.Adapter<IssuesAdapter.IssueVH>() {

    var issues: List<Issue> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    var expandedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.issues_list_item, parent, false)
        return IssueVH(
            view
        )
    }

    override fun getItemCount(): Int = issues.size

    override fun onBindViewHolder(holder: IssueVH, position: Int) {
        val issue = issues[position]
        holder.bindItem(issue)

        if (issue.expanded) {
            expandedPos = position
            fadeInViews(holder.extraInfoLayout)
        } else
            fadeOutViews(holder.extraInfoLayout)

        holder.itemView.setOnClickListener{
            if (issue.expanded) {
                expandedPos = -1
                issue.expanded = false
                launchDetailsScreen(issue, holder.title)
            } else {
                if (expandedPos != -1) {
                    issues[expandedPos].expanded = false
                    notifyItemChanged(expandedPos)
                }
                issue.expanded = true
                notifyItemChanged(position)
            }
        }
    }

    private fun launchDetailsScreen(issue: Issue, itemView: View) {
        mCallback.openDetailsScreen(issue.id, issue.title, itemView)
    }

    class IssueVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.issue_item_title_tv)
        val descriptionTv: TextView = itemView.findViewById(R.id.issue_item_desc_tv)
        val type: TextView = itemView.findViewById(R.id.issue_type_tv)
        val extraInfoLayout: View = itemView.findViewById(R.id.issue_item_extra_info)

        fun bindItem(item: Issue) {
            title.text = item.title
            if (item.description == null || item.description!!.isEmpty())
                descriptionTv.visibility = GONE
            else
                descriptionTv.text = item.description
            type.text = item.type
            title.transitionName = item.id.toString()
        }
    }

}
