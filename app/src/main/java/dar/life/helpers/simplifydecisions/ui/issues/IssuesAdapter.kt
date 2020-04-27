package dar.life.helpers.simplifydecisions.ui.issues

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue

class IssuesAdapter(private val mContext: Context, private val mCallback: OnIssueEditClick) :
    RecyclerView.Adapter<IssuesAdapter.IssueVH>() {

    private var mIssues: List<Issue> = mutableListOf()
    var expandedPos: Int = -1

    private val shortAnimationDuration = mContext.resources.getInteger(
        android.R.integer.config_shortAnimTime).toLong()

    fun setData(issues: List<Issue>){
        mIssues = issues
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.issues_list_item, parent, false)
        return IssueVH(
            view
        )
    }

    override fun getItemCount(): Int = mIssues.size

    override fun onBindViewHolder(holder: IssueVH, position: Int) {
        holder.bindItem(mIssues[position])
        val issue = mIssues[position]

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
                    mIssues[expandedPos].expanded = false
                    notifyItemChanged(expandedPos)
                }
                issue.expanded = true
                notifyItemChanged(position)
            }
        }
    }

    private fun launchDetailsScreen(issue: Issue, itemView: View) {
        mCallback.openIssueDetails(issue.id, issue.title, itemView)
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
            title.transitionName = item.id
        }
    }


    private fun fadeInViews(vararg views: View) {
       views
            .forEach { view: View ->
                view.alpha = 0f
                view.visibility = VISIBLE
                view.animate().alpha(1f).setDuration(shortAnimationDuration)
                    .setListener(null)
            }
    }

    private fun fadeOutViews(vararg views: View) {
        views.forEach { view: View ->
            view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = GONE
                    }
                })
        }
    }
}
