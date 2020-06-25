package dar.life.helpers.simplifydecisions.ui.issues.details

import android.content.Context
import android.graphics.Paint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.data.Opinion

/**
 * Adapter in charge of displaying the [Opinion.Task]s list in the [IssueDetailsFragment]
 *
 * @property context
 * @property mCallback
 */
class IssueTasksAdapter(
    private val context: Context,
    private val mCallback: IssueDetailsTaskChangedListener
) : RecyclerView.Adapter<IssueTasksAdapter.TaskVH>() {
    private var tasksWithLabels: List<Pair<String, Opinion.Task>> = mutableListOf()

    /**
     *
     * Setting the tasks and building a reference string for each task
     *
     * @param issue - The relevant [IssueModel]
     */
    fun setData(issue: IssueModel) {
        tasksWithLabels = mutableListOf<Pair<String, Opinion.Task>>().apply {
            issue.opinions.forEach { (cat, opinions) ->
                opinions.forEach { opinion ->
                    opinion.tasks.forEach {
                        val optionTitle =
                            if (opinion.isOfFirstOption) issue.aTitle
                            else issue.bTitle
                        this.add(Pair("@ $cat\\$optionTitle", it))
                    }
                }
            }
            val complexComparator =
                compareBy<Pair<String, Opinion.Task>>({it.second.isChecked}, {it.first})
            this.sortWith(complexComparator)
        }
        notifyDataSetChanged()
    }

    //true if the last task is being edited (happens in a new task)
    var taskOnEdit: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.task_item, parent, false)
        return TaskVH(
            view,
            mCallback
        )
    }

    override fun getItemCount(): Int = tasksWithLabels.size

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bindItem(tasksWithLabels[position])
        if (taskOnEdit && position == tasksWithLabels.size - 1) {
            !taskOnEdit
            holder.editText.apply {
                isCursorVisible = true
                setSelection(holder.editText.text.length)
                requestFocus()
            }
        }

        holder.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                holder.editText.isCursorVisible = false
                val imm: InputMethodManager? =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(
                    holder.editText.applicationWindowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
            false
        }
    }

    class TaskVH(itemView: View, val mCallback: IssueDetailsTaskChangedListener) :
        RecyclerView.ViewHolder(itemView) {
        val relatedOpinionTv: TextView = itemView.findViewById(R.id.related_opinion_label)
        val editText: EditText = itemView.findViewById<EditText>(R.id.task_item_et)

        init {
            editText.run {
                imeOptions = EditorInfo.IME_ACTION_DONE
                setOnTouchListener { _, _ ->
                    this.isCursorVisible = true
                    return@setOnTouchListener false
                }
            }
        }

        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bindItem(task: Pair<String, Opinion.Task>) {
            relatedOpinionTv.visibility = View.VISIBLE
            relatedOpinionTv.text = "${task.first}"
            editText.apply {
                setText(task.second.text)
                addTextChangedListener(onTextChanged = { text, _, _, _ ->
                    text?.let {
                        task.second.text = it.toString()
                        mCallback.taskTextChanged()
                    }
                })
            }
            checkBox.isChecked = task.second.isChecked
            crossIfTChecked(task.second.isChecked)

            checkBox.setOnCheckedChangeListener { v, isChecked ->
                if (v == checkBox) {
                    task.second.isChecked = isChecked
                    mCallback.taskCheckedChanged()
                    crossIfTChecked(isChecked)

                }
            }
        }

        private fun crossIfTChecked(isChecked: Boolean) {
            editText.paintFlags = if (isChecked) Paint.STRIKE_THRU_TEXT_FLAG
            else Paint.ANTI_ALIAS_FLAG
        }

    }
}