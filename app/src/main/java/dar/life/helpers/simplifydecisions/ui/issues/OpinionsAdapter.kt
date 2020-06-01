package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.ui.UiUtils
import kotlin.math.max

class OpinionsAdapter(private val mContext: Context,
                      private val mCallback: OnOpinionRequest,
                      private val baseIssue: Issue):
    RecyclerView.Adapter<OpinionsAdapter.FactsVH>() {

    var mOpinionsRaws: MutableList<OpinionsRaw> = mutableListOf()
    private val transitionBaseName: String = baseIssue.id.toString()

    fun setData(opinions: MutableMap<String, MutableList<Opinion>>) {

        mOpinionsRaws.clear()

        opinions.forEach { entry ->
            breakToOpinionsRaws(entry)
        }
        notifyDataSetChanged()
    }

    private fun breakToOpinionsRaws(entry: Map.Entry<String, MutableList<Opinion>>) {
        val cat = entry.key
        val (first, second) =
            entry.value.partition { it.isOfFirstOption }
        for (i in 0 until max(first.size, second.size)) {

            val a = first.getOrNull(i)
            val b = second.getOrNull(i)
            val category = if (i == 0) cat else null
            if (a != null || b != null) mOpinionsRaws.add(OpinionsRaw(a, category, b))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsVH {
        val view = LayoutInflater
            .from(mContext).inflate(R.layout.opinions_item, parent, false)
        return FactsVH(view)
    }

    override fun getItemCount(): Int =
        mOpinionsRaws.size+1

    override fun onBindViewHolder(holder: FactsVH, position: Int) {
        if (position != 0) {
            val listPosition = position - 1
            val opRaw = mOpinionsRaws[listPosition]
            holder.bindItem(mOpinionsRaws[listPosition])
            holder.aFrame.background?.let {
                UiUtils.setColorFilter(it, baseIssue.optionAColor)
            }
            holder.bFrame.background?.let {
                UiUtils.setColorFilter(it, baseIssue.optionBColor)
            }
            opRaw.firstOpinion?.let { opinion ->
                holder.aFrame.setOnClickListener {
                    launchOpinionScreen(opinion, holder.aTv, holder.aFrame)
                }
                holder.aTv.transitionName = transitionBaseName + opinion.title
                holder.aFrame.transitionName = transitionBaseName + opinion.title + "frame"
            }
            opRaw.secondOpinion?.let { opinion ->
                holder.bFrame.setOnClickListener {
                    launchOpinionScreen(opinion, holder.bTv, holder.bFrame)
                }
                holder.bTv.transitionName = transitionBaseName + opinion.title
                holder.bFrame.transitionName = transitionBaseName + opinion.title + "frame"

            }
        }else{
            holder.bindFirstItem(mContext)
            holder.aFrame.setOnClickListener{
                mCallback.openNewOpinionScreen(true)
            }
            holder.bFrame.setOnClickListener{
                mCallback.openNewOpinionScreen(false)
            }
        }

    }

        private fun launchOpinionScreen(opinion: Opinion, titleView: View, frameView: View) {
            mCallback.openOpinionScreen(opinion, titleView, frameView)
        }

        class FactsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val aFrame: View = itemView.findViewById(R.id.opinion_a_frame)
            val aTv: TextView = itemView.findViewById(R.id.opinion_a_title)
            val aTasksLeftTv: TextView = itemView.findViewById(R.id.a_tasks_left)
            val bFrame: View = itemView.findViewById(R.id.opinion_b_frame)
            val bTv: TextView = itemView.findViewById(R.id.opinion_b_title)
            val bTasksLeftTv: TextView = itemView.findViewById(R.id.b_tasks_left)
            val catTv: TextView = itemView.findViewById(R.id.category_tv)
            val marginView: View = itemView.findViewById(R.id.margin_view)

            fun bindItem(opRaw: OpinionsRaw) {
                catTv.visibility = if (opRaw.category != null) {
                    catTv.text = opRaw.category
                    marginView.visibility = View.VISIBLE
                    View.VISIBLE
                } else
                    View.INVISIBLE

                aFrame.visibility = if (opRaw.firstOpinion != null) {
                    aTv.text = opRaw.firstOpinion.title
                    if (!opRaw.firstOpinion.isAFact) {
                        aTasksLeftTv.text = opRaw.firstOpinion.tasksLeftText()
                        aTasksLeftTv.visibility = View.VISIBLE
                    }
                    View.VISIBLE
                } else
                    View.INVISIBLE

                bFrame.visibility = if (opRaw.secondOpinion != null) {
                    bTv.text = opRaw.secondOpinion.title
                    if (!opRaw.secondOpinion.isAFact) {
                        bTasksLeftTv.text = opRaw.secondOpinion.tasksLeftText()
                        bTasksLeftTv.visibility = View.VISIBLE
                    }
                    View.VISIBLE
                } else
                    View.INVISIBLE
            }

            fun bindFirstItem(context: Context){
                aFrame.background =
                    ContextCompat.getDrawable(context, R.drawable.add_opinion_item)
                bFrame.background =
                    ContextCompat.getDrawable(context, R.drawable.add_opinion_item)
                catTv.visibility = View.GONE
                aTv.setTextColor(Color.DKGRAY)
                bTv.setTextColor(Color.DKGRAY)
                aTasksLeftTv.visibility = View.GONE
                bTasksLeftTv.visibility = View.GONE
                aTv.text = context.getString(R.string.new_opinion_item_text)
                bTv.text = context.getString(R.string.new_opinion_item_text)
            }
        }

        data class OpinionsRaw(
            val firstOpinion: Opinion? = null,
            val category: String? = null,
            val secondOpinion: Opinion? = null
        )
}