package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.util.LayoutDirection
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Opinion

class FactsAdapter(private val mContext: Context, private val isPositive: Boolean): RecyclerView.Adapter<FactsAdapter.FactsVH>() {

    var mFacts: List<Opinion> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsVH {
        Log.i("OMG", parent.toString())
        val view = LayoutInflater.from(mContext).inflate(R.layout.facts_item, parent, false)
        return FactsVH(view)
    }

    override fun getItemCount(): Int = mFacts.size

    override fun onBindViewHolder(holder: FactsVH, position: Int) {
        holder.itemView.layoutDirection =
            if (isPositive) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL
        val height = mContext.resources.getDimension(R.dimen.grid_item_height).toInt()
        holder.frame.layoutParams = LinearLayout.LayoutParams(0, height,
            mFacts[position].importance.toFloat())
        holder.title.text = mFacts[position].title
    }

    class FactsVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.fact_item_title)
        val frame: View = itemView.findViewById(R.id.fact_frame)
    }
}