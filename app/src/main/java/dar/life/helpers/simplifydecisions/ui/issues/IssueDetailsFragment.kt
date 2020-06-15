package dar.life.helpers.simplifydecisions.ui.issues

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.Transition.TransitionListener
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.Constants
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentIssueDetailsBinding
import dar.life.helpers.simplifydecisions.ui.Instruction
import dar.life.helpers.simplifydecisions.ui.UiUtils
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.iconResFormat
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.nameToColor
import dar.life.helpers.simplifydecisions.ui.UiUtils.Companion.nameToIcon
import dar.life.helpers.simplifydecisions.ui.customview.OptionSumView
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.concurrent.schedule


class IssueDetailsFragment : Fragment(), OnOpinionRequest, OnShowcaseEventListener,
    IssueDetailsTaskChangedListener, AdapterView.OnItemSelectedListener, TransitionListener {

    private var isNewUser: Boolean = false
    private lateinit var decisionIcon: MenuItem
    private var aIconsSpinner: Spinner? = null
    private var aColorsSpinner: Spinner? = null
    private var bIconsSpinner: Spinner? = null
    private var bColorsSpinner: Spinner? = null
    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }
    private var mInstructionsCurrentPos: Int = -1
    private lateinit var mShowcaseView: ShowcaseView
    private val isHelpMode: Boolean
        get() =
            checkHelpMode()
    private lateinit var opinionsAdapter: OpinionsAdapter
    private lateinit var mContext: Context
    private val mViewModel by viewModels<EditIssueViewModel>()

    private var mIssueId: Int = 0
    private var mIssue: IssueModel = IssueModel.DEFAULT_ISSUE

    private var _binding: FragmentIssueDetailsBinding? = null
    private val binding get() = _binding!!

    private var isNewIssue: Boolean = false

    private val args: IssueDetailsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
        if (args.isNew && savedInstanceState == null)
            isNewIssue = true
    }

    override fun onResume() {
        super.onResume()
        if (this::opinionsAdapter.isInitialized)
            opinionsAdapter.setData(mIssue.opinions)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIssueDetailsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        postponeEnterTransition()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.issue_details_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIssueId = args.issueId
        binding.editIssueToolbarTitle.transitionName = mIssueId.toString()
        binding.editIssueToolbarTitle.text = args.issueTitle
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()

        mViewModel.getIssueById(mIssueId)?.observe(viewLifecycleOwner, Observer {
            it?.let {
                mIssue = it
                populateUi(it)
            }
        })
        mViewModel.getAllIssues().observe(viewLifecycleOwner, Observer {
            isNewUser = it.isEmpty() || (it.size == 1 && isNewIssue)
        })
        requireActivity().onBackPressedDispatcher
            .addCallback(mBackPressedCallback)
        Log.i("backSuprise", "addCallback")
        setupSwitchContent()

    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // ************* ClickListeners *************
    private fun switchToCompare() {
        if (hideHelpIfShown())
            return
        binding.switchModeBtn.rotation = 180f
        binding.switchModeBtn.animate().rotationBy(-180F).start()
        ObjectAnimator.ofFloat(
            binding.unfinishedTasksFrame,
            "translationX",
            800f
        ).apply {
            duration = 600
            start()
        }
        UiUtils.fadeOutViews(binding.unfinishedTasksFrame)

        ObjectAnimator.ofFloat(
            binding.compareOptionsFrame,
            "translationX",
            0f
        ).apply {
            duration = 600
            start()
        }
        UiUtils.fadeInViews(binding.compareOptionsFrame, binding.optionsHeaders.root)
    }

    private fun switchToTasks() {
        if (hideHelpIfShown())
            return
        binding.switchModeBtn.rotation = 0f
        binding.switchModeBtn.animate().rotationBy(180f).start()
        ObjectAnimator.ofFloat(
            binding.compareOptionsFrame,
            "translationX",
            -800f
        ).apply {
            duration = 600
            start()
        }

        UiUtils.fadeOutViews(binding.compareOptionsFrame, binding.optionsHeaders.root)

        ObjectAnimator.ofFloat(
            binding.unfinishedTasksFrame,
            "translationX",
            0f
        ).apply {
            duration = 600
            start()
        }
        UiUtils.fadeInViews(binding.unfinishedTasksFrame)
    }

    private fun setupTasksView(issue: IssueModel) {
        if (issue.hasTasks()) {
            binding.noTasksTv.visibility = View.GONE
            val adapter = IssueTasksAdapter(mContext, this)
            adapter.setData(issue)

            binding.allTasksRv.adapter = adapter
            binding.allTasksRv.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        } else {
            binding.noTasksTv.visibility = View.VISIBLE
        }

    }

    private fun setupCompareViews(issue: IssueModel) {
        val linearLayoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.complexOpinionsRv.layoutManager = linearLayoutManager
        binding.complexOpinionsRv.setHasFixedSize(true)
        opinionsAdapter = OpinionsAdapter(
            mContext,
            this,
            mIssue
        )
        binding.complexOpinionsRv.adapter = opinionsAdapter
        opinionsAdapter.setData(issue.opinions)
        binding.complexOpinionsRv.doOnPreDraw { startPostponedEnterTransition() }
        Log.i("backSuprise", "startDelayed")
        linearLayoutManager.scrollToPositionWithOffset(1, 0)
        if (isNewIssue)
            guideNewIssue()
    }

    override fun openNewOpinionScreen(isOfFirstOption: Boolean) {
        if (hideHelpIfShown())
            return
        val action =
            IssueDetailsFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                mIssueId,
                if (isOfFirstOption) nameToColor(mIssue.aColorName, mContext)
                else nameToColor(mIssue.bColorName, mContext)
            )
        action.isNew = true
        action.isNewUser = isNewUser && mIssue.opinions.flatMap { it.value }.isEmpty()
        action.ofFirstOption = isOfFirstOption
        clearCallback()
        findNavController().navigate(
            action
        )

    }

    override fun openOpinionScreen(opinion: Opinion, opinionItemTv: View, opinionItemFrame: View) {

        if (isHelpMode) {
            mShowcaseView.hide()
        } else {
            val action =
                IssueDetailsFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                    mIssue.id,
                    if (opinion.isOfFirstOption)
                        nameToColor(mIssue.aColorName, mContext)
                    else  nameToColor(mIssue.bColorName, mContext)
                )
            action.opinionTitle = opinion.title
            action.ofFirstOption = opinion.isOfFirstOption
            val transitionName = mIssue.id.toString() + opinion.title
            opinionItemTv.transitionName = transitionName
            val fragmentNavigatorExtras = FragmentNavigatorExtras(
                binding.editIssueBottomDrawer to getString(R.string.opinions_bottom_drawer_nav),
                opinionItemFrame to transitionName + getString(R.string.frame_transition_name_extension),
                opinionItemTv to transitionName

            )
            clearCallback()
            findNavController().navigate(
                action, fragmentNavigatorExtras
            )
        }
    }

    private fun backPressed() {
        Log.i("backSuprise", "backPressed")
        if (isHelpMode) {
            Log.i("backSuprise", "backPressed1111")
            mShowcaseView.setOnShowcaseEventListener(OnShowcaseEventListener.NONE)
            mShowcaseView.hide()
            mShowcaseView.isEnabled = false
        } else {
            clearCallback()
            findNavController().popBackStack()
        }
    }



    private fun setupSwitchContent() {
        ObjectAnimator.ofFloat(
            binding.unfinishedTasksFrame,
            "translationX",
            800f
        ).apply {
            duration = 0
            start()
        }
        binding.switchModeBtn.setOnClickListener {
            if (binding.compareOptionsFrame.isVisible) {
                switchToTasks()
            } else {
                switchToCompare()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_issue_title -> {
                editIssueTitle()
                true
            }
            R.id.action_help -> {
                if (hideHelpIfShown())
                    return true
                beginHelpMode()
                true
            }
            R.id.action_create_a_decision -> {
                handleToDecisionClick()
                true
            }
            R.id.action_collaborate_issue -> {
                handleCollaborateClick()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun initToolbar() {
        binding.editIssueToolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.editIssueToolbar)
    }


    private fun populateUi(issue: IssueModel) {
        binding.editIssueToolbarTitle.text = issue.displayedTitle(mContext)
        binding.issueDateTv.text =
            issue.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

        binding.optionsHeaders.run {
            firstOptionTitle.text = issue.aTitle
            firstOptionIcon.setImageDrawable(
                nameToIcon(issue.aColorName, mContext)
            )
            secondOptionTitle.text = issue.bTitle
            secondOptionIcon.setImageDrawable(
                    nameToIcon(issue.bColorName, mContext)
            )
        }

        setupCompareViews(issue)
        setupTasksView(issue)

    }

    private fun guideNewIssue() {
        isNewIssue = false
        Timer("helpMode", false).schedule(100) {
            Log.i("backSuprise", "timedAction")
            if (activity != null)
                viewLifecycleOwner.lifecycleScope.launch {
                    requireActivity().onBackPressedDispatcher
                        .addCallback(mBackPressedCallback)
                    beginHelpMode()
                }
        }
    }

    @SuppressLint("InflateParams")
    private fun editIssueTitle() {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.edit_issue_title_and_spinners, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.text_input_layout)
        val optionAEt: TextView = dialogView.findViewById(R.id.option_a_et)
        val optionBEt: TextView = dialogView.findViewById(R.id.option_b_et)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        textInputLayout.editText?.setText(mIssue.title)
        optionAEt.text = mIssue.aTitle
        optionBEt.text = mIssue.bTitle
        setupEditSpinners(dialogView)
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mIssue.title = textInputLayout.editText?.text.toString()
                mIssue.aTitle = optionAEt.text.toString()
                mIssue.bTitle = optionBEt.text.toString()
                mViewModel.updateIssue(mIssue)
                binding.editIssueToolbarTitle.text = mIssue.displayedTitle(mContext)
                dialogBuilder.dismiss()
            } else {
                Toast.makeText(
                    mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun setupEditSpinners(dialogView: View) {
        val iconsList = UiUtils.getIconsList(mContext)
        val colorNames = UiUtils.colorNames()

        aIconsSpinner = dialogView.findViewById(R.id.a_icons)
        bIconsSpinner = dialogView.findViewById(R.id.b_icons)

        aIconsSpinner!!.adapter = IconsAdapter(mContext, iconsList)
        aIconsSpinner!!.onItemSelectedListener = this
        aIconsSpinner!!.setSelection(colorNames.indexOf(mIssue.aColorName))

        bIconsSpinner!!.adapter = IconsAdapter(mContext, iconsList)
        bIconsSpinner!!.setSelection(colorNames.indexOf(mIssue.bColorName))
        bIconsSpinner!!.onItemSelectedListener = this

    }

    private fun clearCallback() {
        Log.i("backSuprise", "removeCallback")
        mBackPressedCallback.remove()
    }



    private fun handleToDecisionClick() {
        if (isHelpMode) {
            mShowcaseView.hide()
            return
        }
        val alertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.pick_an_option_layout, null)

        val optionBSumView: OptionSumView = dialogView.findViewById(R.id.pick_option_b_sumview)
        val optionASumView: OptionSumView = dialogView.findViewById(R.id.pick_option_a_sumview)

        val (firstScore, secondScore) = mIssue.getOptionsScores()

//            UiUtils.setImportanceColor(this, firstScore, mContext)

        val (firstOpinions, secondOpinions) =
            mIssue.opinions.flatMap { it.value }.partition { it.isOfFirstOption }
        firstOpinions.sortedByDescending { it.importance }
        secondOpinions.sortedByDescending { it.importance }

        optionASumView.apply {
            setBackgroudColor(nameToColor(mIssue.aColorName, mContext))
            setOnClickListener {
                alertDialog.dismiss()
                openNewDecision(true)
            }
            setData(
                nameToIcon(mIssue.aColorName, mContext),
                mIssue.aTitle,
                firstScore,
                firstOpinions.getOrNull(0),
                firstOpinions.getOrNull(1),
                firstOpinions.getOrNull(2)
            )
        }
        optionBSumView.apply {
            setBackgroudColor(nameToColor(mIssue.bColorName, mContext))
            setOnClickListener {
                alertDialog.dismiss()
                openNewDecision(false)
            }
            setData(
                nameToIcon(mIssue.bColorName, mContext),
                mIssue.bTitle,
                secondScore,
                secondOpinions.getOrNull(0),
                secondOpinions.getOrNull(1),
                secondOpinions.getOrNull(2)
            )
        }
        alertDialog.setView(dialogView)
        alertDialog.show()

    }

    private fun openNewDecision(isOpinionA: Boolean) {
        mIssue.toDecision(isOpinionA).also {
            mViewModel.addDecision(it)
            mViewModel.updateIssue(mIssue)
            clearCallback()
            val action =
                IssueDetailsFragmentDirections.actionEditIssueFragmentToDecisionDetailsFragment(
                    it.id,
                    it.title
                )
            action.isNew = true
            findNavController()
                .navigate(
                    action
                )
        }
    }

    private fun beginHelpMode() {
        hideHelpIfShown()
        isNewIssue = false
        val instructions = getInstructions()
        mInstructionsCurrentPos = 0
        if (!isCompareScreen()) switchToCompare()
        mShowcaseView = ShowcaseView.Builder(activity)
            .withHoloShowcase()
            .setShowcaseEventListener(this)
            .hideOnTouchOutside()
            .setStyle(R.style.ShowcaseTheme)
            .setTarget(ViewTarget(binding.complexOpinionsRv[0].findViewById(R.id.opinion_a_frame)))
            .setContentTitle(instructions[0].title)
            .setContentText(instructions[0].text.trimMargin())
            .build()
        mShowcaseView.hideButton()
    }

    private fun hideHelpIfShown(): Boolean {
        if (isHelpMode) {
            mShowcaseView.hide()
            return true
        }
        return false
    }

    private fun handleCollaborateClick() {
        Toast.makeText(mContext, "Upcoming feature (: ", Toast.LENGTH_SHORT).show()
    }

    private fun isCompareScreen(): Boolean {
        return binding.compareOptionsFrame.visibility == View.VISIBLE
    }

    private fun checkHelpMode() = (this@IssueDetailsFragment::mShowcaseView.isInitialized
            && mShowcaseView.isShown)

    /****************
     * ShowCastEventListener
     ****************/
    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {}

    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {

        nextInstruction()
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {}
    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {}

    private fun nextInstruction() {
        mInstructionsCurrentPos++
        mViewModel.issueDetailsInstruc?.let {
            if (it.size > mInstructionsCurrentPos && isHelpMode) {
                mShowcaseView = ShowcaseView.Builder(activity)
                    .withHoloShowcase()
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.ShowcaseTheme)
                    .setTarget(ViewTarget(binding.editIssueToolbar.findViewById(R.id.action_create_a_decision)))
                    .hideOnTouchOutside()
                    .setContentTitle(it[mInstructionsCurrentPos].title)
                    .setContentText(it[mInstructionsCurrentPos].text.trimMargin())
                    .build()
                mShowcaseView.hideButton()
            }
        }
    }

    private fun getInstructions(): List<Instruction> {
        if (mViewModel.issueDetailsInstruc == null) {
            mViewModel.issueDetailsInstruc = listOf<Instruction>(
                Instruction(
                    resources.getStringArray(R.array.instructions_issue_details_title)[0],
                    resources.getStringArray(R.array.instructions_issue_details_body)[0],
                    binding.complexOpinionsRv[0].findViewById(R.id.opinion_a_frame)
                ),
                Instruction(
                    resources.getStringArray(R.array.instructions_issue_details_title)[1],
                    resources.getStringArray(R.array.instructions_issue_details_body)[1],
                    null
                )
            )
        }
        return mViewModel.issueDetailsInstruc!!
    }

    override fun taskTextChanged() {
        mViewModel.updateIssue(mIssue)
    }


    override fun taskCheckedChanged() {
        mViewModel.updateIssue(mIssue)
    }

    //Spinners listener
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            aIconsSpinner -> mIssue.aColorName =
                UiUtils.colorNames().getOrElse(position){ Constants.DEFAULT_A_COLOR }

            bIconsSpinner -> mIssue.bColorName =
                UiUtils.colorNames().getOrElse(position){ Constants.DEFAULT_B_COLOR }

        }
    }

    override fun onTransitionEnd(transition: Transition) {
        if (isNewIssue)
            beginHelpMode()
    }

    override fun onTransitionResume(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }

    override fun onTransitionCancel(transition: Transition) {
    }

    override fun onTransitionStart(transition: Transition) {
    }


}
