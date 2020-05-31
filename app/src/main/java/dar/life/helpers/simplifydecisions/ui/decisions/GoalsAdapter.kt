package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Goal
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class GoalsAdapter(val mContext: Context, mCallback: OnGoalClickListener): RecyclerView.Adapter<GoalsAdapter.GoalVH>() {

    var goalsList: List<Goal> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    //Holding the list position of the current expanded item
    var expandedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.goal_item, parent, false)
        return GoalVH(view)
    }

    override fun getItemCount(): Int = goalsList.size


    override fun onBindViewHolder(holder: GoalVH, position: Int) {
        val goal = goalsList[position]
        holder.populateView(goal, mContext)

        setUpExpandOption(goal, holder, position)
    }

    private fun setUpExpandOption(
        goal: Goal,
        holder: GoalVH,
        position: Int
    )
    {
        if (goal.expanded) {
            Log.d("ADAPTERLOG", "fadein")
            expandedPos = position
            fadeInViews(holder.extraInfoLayout)
        } else {
            fadeOutViews(holder.extraInfoLayout)
            Log.d("ADAPTERLOG", "fadeout")
        }

            holder.itemView.setOnClickListener {
            Log.d("ADAPTERLOG", "clicked1")
            if (goal.expanded) {
                expandedPos = -1
                goal.expanded = false
            } else {
                Log.d("ADAPTERLOG", "clicked2")
                if (expandedPos != -1) {
                    Log.d("ADAPTERLOG", "clicked3")
                    goalsList[expandedPos].expanded = false
                    notifyItemChanged(expandedPos)
                }
                goal.expanded = true
            }
            Log.d("ADAPTERLOG", "clicked4")
            notifyItemChanged(position)
                Log.d("ADAPTERLOG", "clicked5")
            }
    }

    class GoalVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        //TODO: Add CB functionality
        val titleCb: CheckBox = itemView.findViewById(R.id.goal_title_checkbox)
        val titleTv: TextView = itemView.findViewById(R.id.goal_title_tv)
        val warnIv: ImageView = itemView.findViewById(R.id.due_date_passed_iv)
        val dueDateFrame: View = itemView.findViewById(R.id.due_date_frame)
        val dueDateTv: TextView = itemView.findViewById(R.id.due_date)

        val extraInfoLayout: View = itemView.findViewById(R.id.goal_item_extra_info)
        val reminderFrame: View = itemView.findViewById(R.id.reminder_frame_1)
        val reminderTitleTv: TextView = itemView.findViewById(R.id.first_reminder_title)
        val reminderTimeTv: TextView = itemView.findViewById(R.id.first_reminder_date)


        fun populateView(
            goal: Goal,
            context: Context
        ) {
            titleTv.text = goal.name
            dueDateFrame.visibility =
                if (goal.epochDueDate == null) {
                    warnIv.visibility = View.GONE
                    View.GONE
                }else{
                    dueDateTv.text = LocalDate.ofEpochDay(goal.epochDueDate!!)
                        .format(DateTimeFormatter.ofLocalizedDate(
                        FormatStyle.LONG))
                    warnIfDueDatePassed(goal.epochDueDate!!, context)
                    View.VISIBLE
                }
            if (goal.reminder.isActive){
                reminderFrame.visibility = View.VISIBLE
                reminderTimeTv.text = goal.reminder.time.format(DateTimeFormatter.ofLocalizedDateTime(
                    FormatStyle.SHORT
                ))
                reminderTitleTv.text = goal.reminder.title
            }
            else reminderFrame.visibility = View.GONE
        }

        private fun warnIfDueDatePassed(epochDueDate: Long, context: Context) {
            Log.d("ADAPTERLOG", "pos: $adapterPosition warn1")
            warnIv.visibility = if (epochDueDate < LocalDate.now().toEpochDay()){
                Log.d("ADAPTERLOG", "pos: $adapterPosition warn2")
                dueDateTv.setTextColor(ContextCompat.getColor(context, R.color.app_yellow_dark))
                View.VISIBLE
            }
            else {
                dueDateTv.setTextColor(ContextCompat.getColor(context, R.color.secondary_text))
                View.GONE
            }
        }


    }
}
