package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R


class TasksAdapter(private val context: Context): RecyclerView.Adapter<TasksAdapter.TaskVH>() {

    var tasks: MutableList<String> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.task_item, parent, false)
        return TaskVH(view)
    }

    override fun getItemCount(): Int = tasks.size+1

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        Log.d("TOTO", "pos: $position")
        if (position != tasks.size){
            holder.bindItem(tasks[position])
            holder.editText.setOnFocusChangeListener{_, isFocused ->
                Log.d("TOTO", "focus")
                if (!isFocused && holder.editText.text.toString().isNotEmpty()){
                    Log.d("TOTO", "focus2")
                    tasks[position] = holder.editText.text.toString()
                    notifyDataSetChanged()
                }
            }

        }else{
            holder.setLastItem(context.getString(R.string.new_task_hint_text))
            holder.editText.setOnFocusChangeListener{_, isFocused ->
                Log.d("TOTO", "focus")
                if (!isFocused && holder.editText.text.toString().isNotEmpty()){
                    Log.d("TOTO", "focus2")
                    tasks.add(holder.editText.text.toString())
                    notifyDataSetChanged()
                }
            }
        }
        holder.editText.setOnEditorActionListener() { _, actionId, keyEvent ->
            Log.d("TOTO",  "actionId: $actionId")
            Log.d("TOTO", "key event: $keyEvent")
            holder.editText.isCursorVisible = false
            if (actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                val imm: InputMethodManager? =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(
                    holder.editText.applicationWindowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

                if (position==tasks.size)
                {
                    tasks.add(holder.editText.text.toString())
                }else
                    tasks[position] = holder.editText.text.toString()
            }
            false
        }

    }
    class TaskVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val editText: EditText = itemView.findViewById<EditText>(R.id.task_item_et).also {
            it.imeOptions = EditorInfo.IME_ACTION_DONE
            it.setOnTouchListener { _ , _ ->
                Log.d("TOTO", "touched")
                it.isCursorVisible = true
                return@setOnTouchListener false
            }


        }

        fun bindItem(task: String){
            editText.apply {
                setText(task)
                hint = task }


        }

        fun setLastItem(text: String){
            editText.hint = text
        }
    }
}