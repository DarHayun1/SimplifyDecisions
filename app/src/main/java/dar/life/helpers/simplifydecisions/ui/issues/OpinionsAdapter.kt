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
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.ui.UiUtils
import kotlinx.android.synthetic.main.fragment_edit_issue.*

class OpinionsAdapter(private val mContext: Context): RecyclerView.Adapter<OpinionsAdapter.OpinionVH>() {

    private var mOpinions: List<Opinion> = mutableListOf()

    fun setData(opinions: List<Opinion>){
        mOpinions = opinions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpinionVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.grid_item, parent, false)
        return OpinionVH(
            view
        )
    }

    override fun getItemCount(): Int = mOpinions.size

    override fun onBindViewHolder(holder: OpinionVH, position: Int) {
        var opinion = mOpinions[position]

        holder.title.text = opinion.title
    }

    class OpinionVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.grid_item_title)

    }
}
