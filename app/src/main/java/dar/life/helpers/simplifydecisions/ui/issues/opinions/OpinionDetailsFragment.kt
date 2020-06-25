package dar.life.helpers.simplifydecisions.ui.issues.opinions

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.marcinmoskala.arcseekbar.ArcSeekBar
import com.marcinmoskala.arcseekbar.ProgressListener
import com.skydoves.expandablelayout.ExpandableLayout
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_CATEGORY
import dar.life.helpers.simplifydecisions.Constants.NEW_CATEGORY
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.data.Opinion.Companion.GAME_CHANGER
import dar.life.helpers.simplifydecisions.data.Opinion.Companion.HIGH_IMPORTANCE
import dar.life.helpers.simplifydecisions.data.Opinion.Companion.MEDIUM_IMPORTANCE
import dar.life.helpers.simplifydecisions.data.Opinion.Task
import dar.life.helpers.simplifydecisions.databinding.FragmentOpinionDetailsBinding
import dar.life.helpers.simplifydecisions.ui.Instruction
import dar.life.helpers.simplifydecisions.ui.issues.IssuesViewModel
import dar.life.helpers.simplifydecisions.ui.issues.SwipeToDeleteCallback
import dar.life.helpers.simplifydecisions.ui.issues.opinions.OnTaskTextChangedListener
import kotlin.math.abs


/**
 * A [Fragment] in charge of displaying and editing the [Opinion] details
 */
class OpinionDetailsFragment : Fragment(),
    OnTaskTextChangedListener, OnShowcaseEventListener {

    private lateinit var importanceRelativeTv: TextView
    private lateinit var newCategoryEt: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var importanceSb: ArcSeekBar
    private lateinit var importanceSbNumTv: TextView
    private lateinit var importanceSbTv:TextView

    private var isNewOpinion: Boolean = false

    private lateinit var mTasksAdapter: TasksAdapter
    private val mOpinion: Opinion? get() = viewModel.lastUsedOpinion
    private val mIssue: IssueModel? get() = viewModel.lastUsedIssue
    private var category: String = DEFAULT_CATEGORY
    private var _binding: FragmentOpinionDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<IssuesViewModel>()
    private lateinit var mContext: Context

    val args: OpinionDetailsFragmentArgs by navArgs()

    private lateinit var mShowcaseView: ShowcaseView

    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }

    // *****************************
    // ***** Fragment methods *****
    // *****************************

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
        initExpandedViews()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.opinion_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.isNew && savedInstanceState==null) {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val slide = Slide(Gravity.BOTTOM)
        slide.addTarget(binding.opinionDetailsBottomDrawer)
        slide.interpolator = AnimationUtils.loadInterpolator(
            mContext,
            android.R.interpolator.linear_out_slow_in
        )
        requireActivity().window.enterTransition = slide
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

    override fun onDestroyView() {
        hideHelpIfShown()
        _binding = null
        super.onDestroyView()
    }

    // ****************************
    // ***** Helper methods *****
    // ****************************

    /**
     * Populating the UI with the fetched [IssueModel]
     */
    private fun issueFoundInflateFragment(issue: IssueModel) {

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

    /**
     * Getting the ref to the expanded child views and setting the click listeners
     */
    private fun initExpandedViews() {
        binding.expandableCategory.let {expLayout ->
            expLayout.parentLayout.setOnClickListener{ expandOrCollapse(expLayout) }}
        binding.expandableImportance.let {expLayout ->
            expLayout.parentLayout.setOnClickListener{ expandOrCollapse(expLayout) }}
        newCategoryEt = binding.expandableCategory.secondLayout.findViewById(R.id.new_category_et)
        categorySpinner = binding.expandableCategory.secondLayout.findViewById(R.id.category_spinner)
        importanceSb = binding.expandableImportance.secondLayout.findViewById(R.id.importance_sb)
        importanceSbNumTv = binding.expandableImportance.secondLayout.findViewById(R.id.importance_sb_num_tv)
        importanceSbTv = binding.expandableImportance.secondLayout.findViewById(R.id.importance_sb_tv)
        importanceRelativeTv = binding.expandableImportance.secondLayout.findViewById(R.id.importance_relative_tv)
    }

    /**
     * Expanding / Collapsing the [ExpandableLayout]s as needed
     *
     * @param expLayout
     */
    private fun expandOrCollapse(expLayout: ExpandableLayout) {
        if (expLayout.isExpanded) expLayout.collapse()
        else expLayout.expand()
    }

    private fun setupTopDrawer() {
        binding.root.setBackgroundColor(args.opinionColor)
        binding.topDrawerOpinionDetails.apply { elevation = 0f
            setBackgroundColor(Color.TRANSPARENT)}
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

        val isNew = isNewOpinion && args.isNewUser
        isNewOpinion = false
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
            if (isNew) startInstructions()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mOpinion?.title = textInputLayout.editText?.text.toString()
                binding.opinionDetailsToolbarTitle.text = mOpinion?.title
                alertDialog.dismiss()
                if (isNew) startInstructions()
            }else
                Toast.makeText(mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT).show()
        }
        alertDialog.setView(dialogView)
        alertDialog.show()

        showKeyboard(binding.opinionDetailsToolbarTitle)
    }

    private fun showKeyboard(target: View) {
        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT)
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
            mOpinion?.importance = importanceSb.progress
            viewModel.updateIssue(mIssue!!)
        }
        clearCallback()
        findNavController().popBackStack()
    }

    private fun initCategorySpinner() {
        val categories =
            mIssue!!.opinions.keys.toMutableList().apply { add(NEW_CATEGORY) }
        categorySpinner.adapter =
            ArrayAdapter(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                categories)
        categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tempCategory = parent.getItemAtPosition(position) as String
                category = if (tempCategory == NEW_CATEGORY) {
                    newCategoryEt.visibility = View.VISIBLE
                    newCategoryEt.text.toString()
                }
                else {
                    newCategoryEt.visibility = View.INVISIBLE
                    tempCategory
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        newCategoryEt.addTextChangedListener(onTextChanged = {
            text, _, _, _ ->
            category = text.toString()
        })
        val cat = mIssue!!.opinions.toList().first { it.second.contains(mOpinion) }.first
        categorySpinner.setSelection(categories.indexOf(cat))
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
        addSwipeSupport()
        binding.addTaskBtn.setOnClickListener{
            addNewTask()
        }

    }

    private fun addNewTask() {
        hideHelpIfShown()
        mOpinion?.tasks?.let {
            it.add(Task(""))
            mTasksAdapter.newTaskAdded(it)
            val imm =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        }
    }

    private fun addSwipeSupport() {
        val swipeHandler = object : SwipeToDeleteCallback(mContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedIndex = viewHolder.adapterPosition
                val deletedTask = mOpinion?.tasks!![deletedIndex]
                mTasksAdapter.removeAt(viewHolder.adapterPosition)
                mIssue?.let { viewModel.updateIssue(it) }
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

            override fun onMove(
                rv: RecyclerView, vh1: RecyclerView.ViewHolder,
                vh2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.tasksRv)
    }

    private fun setupImportanceBar() {
        val intArray =
            resources.getIntArray(R.array.progressGradientColors)
        importanceSb.setProgressGradient(*intArray)
        importanceSb.onProgressChangedListener = ProgressListener{handlePbChange(it)}

        importanceSb.progress = mOpinion!!.importance
    }

    private fun handlePbChange(value: Int) {
        val impTextView = importanceSbTv
        importanceRelativeTv.text = setRelativeText(value)
        importanceSbNumTv.text = value.toString()

        when {
            value >= GAME_CHANGER -> {
                impTextView.text = getString(R.string.game_changer_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.app_purple))
            }
            value >= HIGH_IMPORTANCE -> {
                impTextView.text = getString(R.string.high_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.prioRed))
            }
            value >= MEDIUM_IMPORTANCE -> {
                impTextView.text = getString(R.string.medium_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.prioOrange))
            }
            else -> {
                impTextView.text = getString(R.string.low_imp)
                impTextView.setTextColor(ContextCompat.getColor(mContext, R.color.app_yellow))
            }
        }
    }

    private fun setRelativeText(imp: Int): String {
        val closestOpinion: Opinion? = mIssue?.opinions?.flatMap { it.value }
            ?.minBy {
                if (it == viewModel.lastUsedOpinion)
                    return@minBy 100
                else
                    abs(it.importance - imp)
            }

        return if (closestOpinion != null && closestOpinion != viewModel.lastUsedOpinion)
            "(${closestOpinion.title} ${closestOpinion.importance})"
        else
            ""
    }

    /**
     * Handling a back button event from the registered callback
     * Hiding the help if it's shown, otherwise letting the user an option to save the progress
     * and return to the previous page
     */
    private fun backPressed() {
        if (!hideHelpIfShown()) {
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
    }

    private fun hideHelpIfShown(): Boolean {
        if (isHelpMode()) {
            mShowcaseView.hide()
            return true
        }
        return false
    }

    private fun isHelpMode() = this::mShowcaseView.isInitialized && mShowcaseView.isShown

    private fun startInstructions() {
        val instruction = getInstruction()
        mShowcaseView = ShowcaseView.Builder(activity)
            .withHoloShowcase()
            .hideOnTouchOutside()
            .setShowcaseEventListener(this)
            .setStyle(R.style.ShowcaseTheme)
            .setTarget(ViewTarget(instruction.view))
            .setContentTitle(instruction.title)
            .setContentText(instruction.text.trimMargin())
            .build()
        mShowcaseView.hideButton()
    }

    private fun getInstruction(): Instruction = Instruction(
        getString(R.string.first_task_title),
        getString(R.string.first_task_text),
        binding.addTaskBtn)

    private fun clearCallback() {
        mBackPressedCallback.isEnabled = false
        mBackPressedCallback.remove()
    }

    // ***************************
    // ***** Event listeners *****
    // ***************************

    override fun onTaskTextChange(pos: Int, text: String) {
        mOpinion?.tasks!![pos].text = text
    }

    override fun onCheckedChanged(pos: Int, checked: Boolean) {
        mOpinion?.checkTask(pos, checked)
    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {
    }

    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
    }

}
