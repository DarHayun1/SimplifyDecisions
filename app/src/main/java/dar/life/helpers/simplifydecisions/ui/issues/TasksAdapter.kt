package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Opinion


class TasksAdapter(private val context: Context, private val mCallback: OnTaskTextChangedListener): RecyclerView.Adapter<TasksAdapter.TaskVH>() {
    var tasks: MutableList<Opinion.Task> = mutableListOf()

    fun setData(newTasks: MutableList<Opinion.Task>){
        tasks = newTasks
        notifyDataSetChanged()
    }

    //true if the last task is being edited (happens in a new task)
    var taskOnEdit: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.task_item, parent, false)
        return TaskVH(view, mCallback)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        Log.d("AAAAA", "onBind - $position,\n ${tasks[position]}")
        holder.bindItem(tasks[position])
        if (taskOnEdit && position == tasks.size - 1) {
            !taskOnEdit
            holder.editText.apply {
                isCursorVisible = true
                setSelection(holder.editText.text.length)
                requestFocus()
            }
        }

        holder.editText.setOnEditorActionListener { _, actionId, _ ->
            Log.d("TASK", "editor $tasks $position $actionId")
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


    fun newTaskAdded(newTasks: MutableList<Opinion.Task>) {
        tasks = newTasks
        taskOnEdit = true
        notifyDataSetChanged()
    }

    class TaskVH(itemView: View, val mCallback: OnTaskTextChangedListener) : RecyclerView.ViewHolder(itemView){
        val editText: EditText = itemView.findViewById<EditText>(R.id.task_item_et).also {
            it.imeOptions = EditorInfo.IME_ACTION_DONE
            it.setOnTouchListener { _ , _ ->
                it.isCursorVisible = true
                return@setOnTouchListener false
            }
        }
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bindItem(task: Opinion.Task){
            editText.apply {
                setText(task.text)
                addTextChangedListener(onTextChanged = {
                    text, _, _, _ ->
                    text?.let {
                        mCallback.onTaskTextChange(adapterPosition, it.toString())
                    }
                })
            }
            checkBox.isChecked = task.isChecked
            crossIfTChecked(task.isChecked)

            checkBox.setOnCheckedChangeListener { v, isChecked ->
                Log.d("AAAAA", "checklistener - $adapterPosition")
                if (v==checkBox) {
                    mCallback.onCheckedChanged(adapterPosition, isChecked)
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