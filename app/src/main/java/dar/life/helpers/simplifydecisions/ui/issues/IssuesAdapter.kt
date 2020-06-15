package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class IssuesAdapter(private val mContext: Context, private val mCallback: OnDetailsRequest) :
    RecyclerView.Adapter<IssuesAdapter.IssueVH>() {

    var issues: List<IssueModel> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    //Holding the list position of the current expanded item
    private var expandedPos: Int = -1

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
        holder.bindItem(issue, mContext)

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

    private fun launchDetailsScreen(issue: IssueModel, itemView: View) {
        mCallback.openDetailsScreen(issue.id, issue.title, itemView)
    }

    class IssueVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.issue_item_title_tv)
        val dateTv: TextView = itemView.findViewById(R.id.issue_item_date_tv)
        val numOfOpinionsTv: TextView = itemView.findViewById(R.id.issue_item_num_of_opinions)
        val extraInfoLayout: View = itemView.findViewById(R.id.issue_item_extra_info)

        fun bindItem(item: IssueModel, context: Context) {
            title.text = item.displayedTitle(context)
            dateTv.text = item.date.format(
                DateTimeFormatter.ofLocalizedDate(
                    FormatStyle.LONG))
            numOfOpinionsTv.text = item.opinions.flatMap { it.value }.size.toString()
            title.transitionName = item.id.toString()
        }
    }

}
