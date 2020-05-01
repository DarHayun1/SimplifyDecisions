package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.text.Editable
import android.text.Selection.setSelection
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R


class TasksAdapter(private val context: Context, private val mCallback: OnTaskTextChangedListener): RecyclerView.Adapter<TasksAdapter.TaskVH>() {
    var tasks: MutableList<String> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //true if the last task is being edited (happens in a new task)
    var taskOnEdit: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.task_item, parent, false)
        return TaskVH(view, tasks, mCallback)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
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


    fun newTaskAdded(newTasks: MutableList<String>) {
        tasks = newTasks
        taskOnEdit = true
        notifyDataSetChanged()
    }

    class TaskVH(itemView: View, tasks: MutableList<String>, val mCallback: OnTaskTextChangedListener) : RecyclerView.ViewHolder(itemView){
        val editText: EditText = itemView.findViewById<EditText>(R.id.task_item_et).also {
            it.imeOptions = EditorInfo.IME_ACTION_DONE
            it.setOnTouchListener { _ , _ ->
                it.isCursorVisible = true
                return@setOnTouchListener false
            }


        }

        fun bindItem(task: String){
            editText.apply {
                setText(task)
                hint = task
                addTextChangedListener(onTextChanged = {
                    text, _, _, _ ->
                    text?.let {
                        mCallback.onTaskTextChange(adapterPosition, it.toString())
                    }
                })
            }
        }

    }
}