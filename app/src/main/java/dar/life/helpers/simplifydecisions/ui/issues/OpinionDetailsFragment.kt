package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.marcinmoskala.arcseekbar.ProgressListener
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_CATEGORY
import dar.life.helpers.simplifydecisions.Constants.NEW_CATEGORY
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.data.Opinion.Companion.GAME_CHANGER
import dar.life.helpers.simplifydecisions.data.Opinion.Companion.HIGH_IMPORTANCE
import dar.life.helpers.simplifydecisions.data.Opinion.Companion.MEDIUM_IMPORTANCE
import dar.life.helpers.simplifydecisions.data.Opinion.Task
import dar.life.helpers.simplifydecisions.databinding.FragmentOpinionDetailsBinding


/**
 * A simple [Fragment] subclass.
 */
class OpinionDetailsFragment : Fragment(),
    OnTaskTextChangedListener {

    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }

    private var isNewOpinion: Boolean = false

    private lateinit var mTasksAdapter: TasksAdapter
    private val mOpinion: Opinion? get() = viewModel.lastUsedOpinion
    private val mIssue: Issue? get() = viewModel.lastUsedIssue
    private var category: String = DEFAULT_CATEGORY
    private var _binding: FragmentOpinionDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: IssuesViewModel
    private lateinit var mContext: Context
    val args: OpinionDetailsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOpinionDetailsBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.opinion_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.opinionTitle == "New Opinion") {
            isNewOpinion = true
            setupTopDrawer()
        }
        else{
            val transitionName = args.issueId.toString() + args.opinionTitle
            binding.opinionDetailsToolbarTitle.transitionName = transitionName
            binding.opinionDetailsToolbarTitle.text = args.opinionTitle

            binding.topDrawerOpinionDetails.transitionName = transitionName +
                    getString(R.string.frame_transition_name_extension)
            binding.topDrawerOpinionDetails.setBackgroundColor(args.opinionColor)
            Handler().postDelayed({
                setupTopDrawer()
            }, 300)


        }

    }

    private fun setupTopDrawer() {
        binding.topDrawerOpinionDetails.elevation = 0f
        binding.root.setBackgroundColor(args.opinionColor)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val slide = Slide(Gravity.BOTTOM)
        slide.addTarget(binding.opinionDetailsBottomDrawer)
        slide.interpolator = AnimationUtils.loadInterpolator(
            mContext,
            android.R.interpolator.linear_out_slow_in
        )
        requireActivity().window.enterTransition = slide
        viewModel = ViewModelProvider(this).get(IssuesViewModel::class.java)
        initToolbar()
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_opinion_title -> editTitle()
            R.id.action_save_opinion -> saveClicked()
        }
        return super.onOptionsItemSelected(item)
    }



    private fun editTitle() {
        val alertDialog: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.edit_title_layout, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.text_input_layout)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        textInputLayout.hint = getString(R.string.opinion_title)
        mOpinion?.let {
            textInputLayout.editText?.setText(mOpinion?.title)
        }

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mOpinion?.title = textInputLayout.editText?.text.toString()
                binding.opinionDetailsToolbarTitle.text = mOpinion?.title
                alertDialog.dismiss()
            }else
                Toast.makeText(mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT).show()
        }
        alertDialog.setView(dialogView)
        alertDialog.show()

        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(binding.opinionDetailsToolbarTitle, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun initViews() {

        viewModel.getIssueById(args.issueId).observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.lastUsedIssue = it
                issueFoundInflateFragment(it) } })
        requireActivity().onBackPressedDispatcher
            .addCallback(mBackPressedCallback)
        if(isNewOpinion) editTitle()
    }

    private fun saveClicked() {
        mIssue?.let {
            if (category == "")
                category = DEFAULT_CATEGORY
            it.changeOpinionCategory(mOpinion!!, category)
            mOpinion?.importance = binding.importanceSb.progress
            viewModel.updateIssue(mIssue!!)
        }
        clearCallback()
        findNavController().popBackStack()
    }

    private fun issueFoundInflateFragment(issue: Issue) {

        binding.relatedIssueTitle.text = issue.displayedTitle(mContext)

        viewModel.lastUsedOpinion =
            issue.opinions.values.flatten().firstOrNull { it.title == args.opinionTitle }
        if (mOpinion == null)
            viewModel.lastUsedOpinion = Opinion(getString(R.string.default_opinion_title),
                isOfFirstOption = args.ofFirstOption)
                .also {newOpinion -> mIssue!!.opinions[DEFAULT_CATEGORY]!!.add(newOpinion)}

        binding.opinionDetailsToolbarTitle.text = mOpinion?.title
        setupImportanceBar()
        setupTasks()

        initCategorySpinner()
    }

    private fun initCategorySpinner() {
        val categories =
            mIssue!!.opinions.keys.toMutableList().apply { add(NEW_CATEGORY) }
        binding.categorySpinner.adapter =
            ArrayAdapter(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                categories)
        binding.categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tempCategory = parent.getItemAtPosition(position) as String
                category = if (tempCategory == NEW_CATEGORY) {
                    binding.newCategoryEt.visibility = View.VISIBLE
                    binding.newCategoryEt.text.toString()
                }
                else {
                    binding.newCategoryEt.visibility = View.INVISIBLE
                    tempCategory
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.newCategoryEt.addTextChangedListener(onTextChanged = {
            text, _, _, _ ->
            category = text.toString()
        })
        val cat = mIssue!!.opinions.toList().first { it.second.contains(mOpinion) }.first
        binding.categorySpinner.setSelection(categories.indexOf(cat))
    }

    private fun initToolbar() {
        binding.editOpinionToolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.editOpinionToolbar)
    }


    private fun setupTasks() {
        mTasksAdapter =
            TasksAdapter(
                mContext,
                this
            )
        binding.tasksRv.adapter = mTasksAdapter
        binding.tasksRv.layoutManager = LinearLayoutManager(mContext, VERTICAL, false)
        mTasksAdapter.setData(mOpinion!!.tasks)
        val swipeHandler = object : SwipeToDeleteCallback(mContext){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedIndex = viewHolder.adapterPosition
                val deletedTask = mOpinion?.tasks!![deletedIndex]
                mTasksAdapter.removeAt(viewHolder.adapterPosition)
                mIssue?.let { viewModel.updateIssue(it)}
                val snackbar: Snackbar = Snackbar
                    .make(
                        binding.tasksRv,
                        "Task deleted!",
                        Snackbar.LENGTH_LONG
                    )
                snackbar.setAction("UNDO") { // undo is selected, restore the deleted item
                    mTasksAdapter.restoreItem(deletedTask, deletedIndex)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }

            override fun onMove(rv: RecyclerView, vh1: RecyclerView.ViewHolder,
                                vh2: RecyclerView.ViewHolder): Boolean {
                return false
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.tasksRv)
        binding.addTaskBtn.setOnClickListener{
            mOpinion?.tasks?.let{
                it.add(Task(""))
                mTasksAdapter.newTaskAdded(it)
            }
        }

    }

    private fun setupImportanceBar() {
        val intArray =
            resources.getIntArray(R.array.progressGradientColors)
        binding.importanceSb.setProgressGradient(*intArray)
        binding.importanceSb.onProgressChangedListener = ProgressListener{handlePbChange(it)}

        binding.importanceSb.progress = mOpinion!!.importance
    }

    private fun handlePbChange(it: Int) {
        val impTextView = binding.importanceSbTv
        when {
            it >= GAME_CHANGER -> {
                impTextView.text = getString(R.string.game_changer_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.app_purple))
            }
            it >= HIGH_IMPORTANCE -> {
                impTextView.text = getString(R.string.high_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.prioRed))
            }
            it >= MEDIUM_IMPORTANCE -> {
                impTextView.text = getString(R.string.medium_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.prioOrange))
            }
            else -> {
                impTextView.text = getString(R.string.low_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.app_yellow))
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        view.startAnimation(animate)
    }

    override fun onTaskTextChange(pos: Int, text: String) {
        mOpinion?.tasks!![pos].text = text
    }

    override fun onCheckedChanged(pos: Int, checked: Boolean) {
        mOpinion?.checkTask(pos, checked)

    }

    private fun backPressed() {
            AlertDialog.Builder(mContext)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.alertdialog_title))
                .setMessage(resources.getString(R.string.unsaved_progress_message))
                .setPositiveButton(
                    resources.getString(R.string.save_button_text)
                ) { _, _ ->
                    saveClicked()
                }
                .setNegativeButton(
                    resources.getString(R.string.ignore_button_text)
                ) { _, _ ->
                    clearCallback()
                    findNavController().popBackStack()
                }
                .show()
    }

    private fun clearCallback() {
        mBackPressedCallback.remove()
    }

}
