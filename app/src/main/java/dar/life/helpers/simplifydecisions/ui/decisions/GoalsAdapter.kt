package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Goal
import dar.life.helpers.simplifydecisions.ui.UiUtils
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeInViews
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.fadeOutViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class GoalsAdapter(val mContext: Context,val mCallback: OnGoalClickListener): RecyclerView.Adapter<GoalsAdapter.GoalVH>() {

    var goalsList: MutableList<Goal> = mutableListOf()
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
        setUpIsDone(goal, holder, position)
        setUpExpandOption(goal, holder, position)
        setupMoreOptions(holder, goal, position)
    }

    private fun setupMoreOptions(
        holder: GoalVH,
        goal: Goal,
        position: Int
    ) {
        holder.settingsBtn.setOnClickListener {
            val popupMenu = PopupMenu(mContext, holder.settingsBtn)
            popupMenu.inflate(R.menu.goal_item_menu)
            popupMenu.menu.findItem(R.id.action_mark_goal_done).title =
                if (goal.isDone) mContext.getString(R.string.goal_mark_unfinished)
                else mContext.getString(R.string.goal_mark_done)
            popupMenu.setOnMenuItemClickListener {
                popupMenu.dismiss()
                when (it.itemId) {
                    R.id.action_mark_goal_done -> {
                        holder.titleCb.isChecked = !holder.titleCb.isChecked
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_edit_goal -> {
                        mCallback.onEditGoalRequest(position)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_delete_goal -> {
                        mCallback.deleteGoal(goal)
                        return@setOnMenuItemClickListener true
                    }
                    else ->
                        return@setOnMenuItemClickListener false
                }
            }
            popupMenu.show()
        }
    }

    private fun setUpIsDone(goal: Goal, holder: GoalVH, position: Int) {
        holder.titleCb.isChecked = goal.isDone
        paintIfDone(holder, goal)

        holder.titleCb.setOnCheckedChangeListener{
                view, isChecked ->
            if (view == holder.titleCb) {
                goal.isDone = isChecked
                paintIfDone(holder, goal)
                holder.warnIfDueDatePassed(goal, mContext)
                mCallback.onGoalChecked(goal)
            }
        }
    }

    private fun paintIfDone(
        holder: GoalVH,
        goal: Goal
    ) {
        holder.itemView.background?.let {
            val backgroundColorId =
                if (goal.isDone)
                    R.color.app_green_light_trans
                else
                    android.R.color.background_light
            UiUtils.setColorFilter(
                it,
                ContextCompat.getColor(mContext, backgroundColorId)
            )
        }
    }

    private fun setUpExpandOption(
        goal: Goal,
        holder: GoalVH,
        position: Int
    )
    {
        Log.d("expandbug", "expPos: $expandedPos, ${goal.expanded}, Goal: $goal")
        if (goal.expanded) {
            expandedPos = position
            fadeInViews(holder.extraInfoLayout)
        } else {
            fadeOutViews(holder.extraInfoLayout)
        }

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
        val titleTv: TextView = itemView.findViewById(R.id.goal_title_tv)
        val warnIv: ImageView = itemView.findViewById(R.id.due_date_passed_iv)
        val dueDateFrame: View = itemView.findViewById(R.id.due_date_frame)
        val dueDateTv: TextView = itemView.findViewById(R.id.due_date)

        val extraInfoLayout: View = itemView.findViewById(R.id.goal_item_extra_info)
        val reminderFrame: View = itemView.findViewById(R.id.reminder_frame_1)
        val reminderTitleTv: TextView = itemView.findViewById(R.id.first_reminder_title)
        val reminderTimeTv: TextView = itemView.findViewById(R.id.first_reminder_date)

        val settingsBtn: View = itemView.findViewById(R.id.goal_item_actions_iv)


        fun populateView(
            goal: Goal,
            context: Context
        ) {
            titleTv.text = goal.name
            dueDateFrame.visibility =
                if (goal.epochDueDate == null) {
                    warnIv.visibility = View.GONE
                    View.GONE
                } else {
                    dueDateTv.text = LocalDate.ofEpochDay(goal.epochDueDate!!)
                        .format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.LONG
                            )
                        )
                    warnIfDueDatePassed(goal, context)
                    View.VISIBLE
                }
            if (goal.reminder.isActive) {
                reminderFrame.visibility = View.VISIBLE
                reminderTimeTv.text = goal.reminder.time.format(
                    DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.SHORT
                    )
                )
                reminderTitleTv.text = goal.reminder.title
            } else reminderFrame.visibility = View.GONE
        }

        fun warnIfDueDatePassed(goal: Goal, context: Context) {
            warnIv.visibility = if (
                !goal.isDone &&
                goal.epochDueDate != null &&
                goal.epochDueDate!! < LocalDate.now().toEpochDay()){
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
