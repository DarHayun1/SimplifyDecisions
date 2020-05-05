package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Opinion
import kotlin.math.max

class FactsAdapter(private val mContext: Context,
                   private val mCallback: OnOpinionRequest,
                    private val transitionBaseName: String):
    RecyclerView.Adapter<FactsAdapter.FactsVH>() {

    var mOpinionsRaws: MutableList<OpinionsRaw> = mutableListOf()

    fun setData(opinions: MutableMap<String, MutableList<Opinion>>) {

        mOpinionsRaws.clear()

        opinions.forEach { entry ->
            val cat = entry.key
            val (first, second) =
                entry.value.partition { it.isOfFirstOption }
            for (i in 0 until max(first.size, second.size)) {

                val a = if (i < first.size) first[i] else null
                val b = if (i < (second.size)) second[i] else null
                val category = if (i == 0) cat else null
                if (a!=null || b!=null) mOpinionsRaws.add(OpinionsRaw(a, category, b))
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsVH {
        val view = LayoutInflater
            .from(mContext).inflate(R.layout.opinions_item, parent, false)
        return FactsVH(view)
    }

    override fun getItemCount(): Int =
        mOpinionsRaws.size

    override fun onBindViewHolder(holder: FactsVH, position: Int) {
//        holder.itemView.layoutDirection =
//            if (isPositive) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL
//        holder.frame.setBackgroundResource(
//            if (isPositive) R.drawable.positive_fact_ripple
//            else R.drawable.negative_fact_ripple
//        )
        val opRaw = mOpinionsRaws[position]
        holder.bindItem(mOpinionsRaws[position])
        holder.aFrame.background?.colorFilter =
            BlendModeColorFilter(Color.parseColor("#99c16c"), BlendMode.SRC_ATOP)
        holder.bFrame.background?.colorFilter =
            BlendModeColorFilter(Color.parseColor("#e02032"), BlendMode.SRC_ATOP)
        opRaw.firstOpinion?.let {opinion ->
            holder.aFrame.setOnClickListener {
                    launchOpinionScreen(opinion, holder.aTv) }
            holder.aTv.transitionName = transitionBaseName + opinion.title
        }
        opRaw.secondOpinion?.let {opinion ->
            holder.bFrame.setOnClickListener {
                launchOpinionScreen(opinion, holder.bTv) }
            holder.bTv.transitionName = transitionBaseName + opinion.title

        }

    }

        private fun launchOpinionScreen(opinion: Opinion, titleView: View) {
            mCallback.openOpinionScreen(opinion, titleView)
        }

        class FactsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val aFrame: View = itemView.findViewById(R.id.opinion_a_frame)
            val aTv: TextView = itemView.findViewById(R.id.opinion_a_title)
            val bFrame: View = itemView.findViewById(R.id.opinion_b_frame)
            val bTv: TextView = itemView.findViewById(R.id.opinion_b_title)
            val catTv: TextView = itemView.findViewById(R.id.category_tv)

            fun bindItem(opRaw: OpinionsRaw) {
                catTv.visibility = if (opRaw.category != null) {
                    catTv.text = opRaw.category
                    View.VISIBLE
                } else
                    View.INVISIBLE

                aFrame.visibility = if (opRaw.firstOpinion != null) {
                    aTv.text = opRaw.firstOpinion.title
                    View.VISIBLE
                } else
                    View.INVISIBLE

                bFrame.visibility = if (opRaw.secondOpinion != null) {
                    bTv.text = opRaw.secondOpinion.title
                    View.VISIBLE
                } else
                    View.INVISIBLE
            }
        }

        data class OpinionsRaw(
            val firstOpinion: Opinion? = null,
            val category: String? = null,
            val secondOpinion: Opinion? = null
        )
}