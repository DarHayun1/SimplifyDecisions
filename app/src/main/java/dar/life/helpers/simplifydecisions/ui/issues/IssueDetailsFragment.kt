package dar.life.helpers.simplifydecisions.ui.issues

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentIssueDetailsBinding
import dar.life.helpers.simplifydecisions.ui.Instruction
import dar.life.helpers.simplifydecisions.ui.UiUtils
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class IssueDetailsFragment : Fragment(), OnOpinionRequest, OnShowcaseEventListener,
    IssueDetailsTaskChangedListener {

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
    private lateinit var mViewModel: EditIssueViewModel

    private var mIssueId: Int = 0
    private var mIssue: Issue = Issue.DEFAULT_ISSUE

    private var _binding: FragmentIssueDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: IssueDetailsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
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

        mViewModel = ViewModelProvider(this).get(EditIssueViewModel::class.java)
        mViewModel.getIssueById(mIssueId)?.observe(viewLifecycleOwner, Observer {
            it?.let {
                mIssue = it
                populateUi(it)
            }
        })
        postponeEnterTransition()
        requireActivity().onBackPressedDispatcher
            .addCallback(mBackPressedCallback)
        setupSwitchContent()
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
        binding.switchBtn.setOnClickListener {
            if (binding.compareOptionsFrame.isVisible) {
                switchToTasks()
            } else {
                switchToComare()
            }
        }
    }

    private fun switchToComare() {
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_issue_title -> {
                editIssueTitle()
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

    private fun populateUi(issue: Issue) {
        binding.editIssueToolbarTitle.text = issue.displayedTitle(mContext)
        binding.issueDateTv.text =
            issue.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

        binding.optionsHeaders.run {
            firstOptionTitle.text = issue.optionAName
            firstOptionIcon.setImageDrawable(
                mContext.getDrawable(
                    mContext.resources.getIdentifier(
                        issue.optionAIconName, "drawable", mContext.packageName
                    )
                )
            )
            secondOptionTitle.text = issue.optionBName
            secondOptionIcon.setImageDrawable(
                mContext.getDrawable(
                    mContext.resources.getIdentifier(
                        issue.optionBIconName, "drawable", mContext.packageName
                    )
                )
            )
        }

        setupCompareViews(issue)
        setupTasksView(issue)


    }

    private fun setupTasksView(issue: Issue) {
        val adapter = IssueTasksAdapter(mContext, this)
        adapter.setData(issue)

        binding.allTasksRv.adapter = adapter
        binding.allTasksRv.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

    }

    private fun setupCompareViews(issue: Issue) {
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
        linearLayoutManager.scrollToPositionWithOffset(1, 0)
    }

    @SuppressLint("InflateParams")
    private fun editIssueTitle() {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.edit_title_layout, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.text_input_layout)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        textInputLayout.editText?.setText(mIssue.title)
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mIssue.title = textInputLayout.editText?.text.toString()
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

    override fun openOpinionScreen(opinion: Opinion, opinionItemTv: View, opinionItemFrame: View) {

        if (isHelpMode) {
            mShowcaseView.hide()
        } else {
            val action =
                IssueDetailsFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                    mIssue.id,
                    if (opinion.isOfFirstOption) mIssue.optionAColor else mIssue.optionBColor
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

    private fun clearCallback() {
        mBackPressedCallback.remove()
    }

    override fun openNewOpinionScreen(isOfFirstOption: Boolean) {
        if (isHelpMode)
            mShowcaseView.hide()
        else {
            val action =
                IssueDetailsFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                    mIssueId,
                    if (isOfFirstOption) mIssue.optionAColor else mIssue.optionBColor
                )
            action.ofFirstOption = isOfFirstOption
            clearCallback()
            findNavController().navigate(
                action
            )
        }
    }

    private fun handleToDecisionClick() {
        mIssue.toDecision().also {
            mViewModel.addDecision(it)
            mViewModel.updateIssue(mIssue)
            clearCallback()
            findNavController()
                .navigate(
                    IssueDetailsFragmentDirections.actionEditIssueFragmentToDecisionDetailsFragment(
                        it.id,
                        it.title
                    )
                )
        }
    }

    private fun handleCollaborateClick() {
        val instructions = getInstructions()
        mInstructionsCurrentPos = 0
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

    private fun backPressed() {
        if (isHelpMode) {
            mShowcaseView.setOnShowcaseEventListener(OnShowcaseEventListener.NONE)
            mShowcaseView.hide()
            mShowcaseView.isEnabled = false
        } else {
            clearCallback()
            findNavController().popBackStack()
        }
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
            if (it.size > mInstructionsCurrentPos) {
                mShowcaseView = ShowcaseView.Builder(activity)
                    .withHoloShowcase()
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.ShowcaseTheme)
                    .setTarget(ViewTarget(binding.complexOpinionsRv[0].findViewById(R.id.opinion_b_frame)))
                    .hideOnTouchOutside()
                    .setContentTitle(it[mInstructionsCurrentPos].title)
                    .setContentText(it[mInstructionsCurrentPos].text.trimMargin())
                    .build()
                mShowcaseView.hideButton()
            } else {
                isHelpMode
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
                    binding.complexOpinionsRv[0].findViewById(R.id.opinion_b_frame)
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


}
