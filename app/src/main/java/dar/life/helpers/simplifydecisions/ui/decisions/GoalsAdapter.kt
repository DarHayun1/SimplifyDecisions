package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Goal
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews
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
        holder.populateView(goal)

        setUpExpandOption(goal, holder, position)
    }

    private fun setUpExpandOption(
        goal: Goal,
        holder: GoalVH,
        position: Int
    )
    {
        if (goal.expanded) {
            expandedPos = position
            fadeInViews(holder.extraInfoLayout)
        } else
            fadeOutViews(holder.extraInfoLayout)

        holder.itemView.setOnClickListener {
            if (goal.expanded) {
                expandedPos = -1
                goal.expanded = false
            } else {
                if (expandedPos != -1) {
                    goalsList[expandedPos].expanded = false
                    notifyItemChanged(expandedPos)
                }
                goal.expanded = true
            }
            notifyItemChanged(position)
        }
    }

    class GoalVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleCb: CheckBox = itemView.findViewById(R.id.goal_title_checkbox)
        val dueDateFrame: View = itemView.findViewById(R.id.due_date_frame)
        val dueDateTv: TextView = itemView.findViewById(R.id.due_date)

        val extraInfoLayout: View = itemView.findViewById(R.id.goal_item_extra_info)
        val reminderFrame1: View = itemView.findViewById(R.id.reminder_frame_1)
        val firstReminderTv: TextView = itemView.findViewById(R.id.first_reminder_title)
        /*
        /Currently not in use
        val secondReminderTv: TextView = itemView.findViewById(R.id.second_reminder_title)
        val thirdReminderTv: TextView = itemView.findViewById(R.id.third_reminder_title)
     */
        fun populateView(goal: Goal) {
            titleCb.text = goal.name
            dueDateFrame.visibility =
                if (goal.dueDate == null) {
                    View.GONE
                }else{
                    dueDateTv.text = goal.dueDate?.format(DateTimeFormatter.ofLocalizedDate(
                        FormatStyle.LONG))
                    View.VISIBLE
                }
            if (goal.reminders.size>0){
                firstReminderTv.text = goal.reminders[0].name
            }

        }


    }
}
