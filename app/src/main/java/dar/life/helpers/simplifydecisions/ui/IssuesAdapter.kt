package dar.life.helpers.simplifydecisions.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue

class IssuesAdapter(private val mContext: Context): RecyclerView.Adapter<IssuesAdapter.IssueVH>() {

    private var mIssues: List<Issue> = mutableListOf()

    fun setData(issues: List<Issue>){
        mIssues = issues
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.issues_list_item, parent, false)
        return IssueVH(view)
    }

    override fun getItemCount(): Int = mIssues.size

    override fun onBindViewHolder(holder: IssueVH, position: Int) {
        holder.textView.text = mIssues[position].title

    }

    class IssueVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView = itemView.findViewById<TextView>(R.id.issue_item_text)
    }
}
