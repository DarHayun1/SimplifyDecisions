package dar.life.helpers.simplifydecisions.ui.issues

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentIssueDetailsBinding
import kotlinx.android.synthetic.main.fragment_issue_details.*
import kotlinx.android.synthetic.main.options_headers_layout.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class IssueDetailsFragment : Fragment(), OnOpinionRequest {

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

    private fun handleCollaborateClick() {
        Toast.makeText(mContext, "Upcoming feature (: ", Toast.LENGTH_SHORT).show()
    }

    private fun initToolbar() {
        binding.editIssueToolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.editIssueToolbar)
    }

    private fun populateUi(issue: Issue) {
        binding.editIssueToolbarTitle.text = issue.title
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
        val dialogTitle: TextView = dialogView.findViewById(R.id.edit_title_header_tv)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        dialogTitle.text = getString(R.string.edit_issue_title_label)
        textInputLayout.editText?.setText(mIssue.title)
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        saveBtn.setOnClickListener {
            mIssue.title = textInputLayout.editText?.text.toString()
            binding.editIssueToolbarTitle.text = mIssue.title
            dialogBuilder.dismiss()
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    override fun openOpinionScreen(opinion: Opinion, opinionItemTv:View, opinionItemFrame: View) {

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
        findNavController().navigate(
            action, fragmentNavigatorExtras
        )
    }

    override fun openNewOpinionScreen(isOfFirstOption: Boolean) {
        val action =
            IssueDetailsFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                mIssueId,
                if (isOfFirstOption) mIssue.optionAColor else mIssue.optionBColor
            )
        action.ofFirstOption = isOfFirstOption
        findNavController().navigate(
            action
        )
    }

    private fun handleToDecisionClick() {
        mIssue.toDecision().also {
            mViewModel.addDecision(it)
            mViewModel.updateIssue(mIssue)
            findNavController()
                .navigate(
                    IssueDetailsFragmentDirections.actionEditIssueFragmentToDecisionDetailsFragment(
                        it.id,
                        it.title
                    )
                )
        }
    }

}
