package dar.life.helpers.simplifydecisions.ui

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentEditIssueBinding
import dar.life.helpers.simplifydecisions.ui.issues.*
import kotlinx.android.synthetic.main.fragment_edit_issue.*
import kotlinx.android.synthetic.main.opinions_layout.*
import kotlinx.android.synthetic.main.options_headers_layout.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class EditIssueFragment : Fragment(),
    OnOpinionRequest {
    private lateinit var opinionsAdapter: FactsAdapter
    private lateinit var mContext: Context
    private lateinit var mViewModel: EditIssueViewModel

    private var mIssueId: Int = 0
    private var mIssue: Issue = Issue.DEFAULT_ISSUE

    private var _binding: FragmentEditIssueBinding? = null
    private val binding get() = _binding!!

    private val args: EditIssueFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition =
//            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
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
        _binding = FragmentEditIssueBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIssueId = args.issueId
        binding.issueTitleTv.transitionName = mIssueId.toString()
        binding.issueTitleTv.text = args.issueTitle
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EditIssueViewModel::class.java)

        mViewModel.getIssueById(mIssueId)?.observe(viewLifecycleOwner, Observer {
            it?.let {
                mIssue = it
                populateUi(it)
            }
        })
        initViews()
    }

    private fun initViews() {
        binding.addOpinionFab.setOnClickListener{navigateToNewOpinion()}
        to_decision_button.setOnClickListener {handleToDecisionClick()}
        edit_issue_title_icon.setOnClickListener { handleEditTitleClick() }
        postponeEnterTransition()
    }

    private fun navigateToNewOpinion() {
        val action =
            EditIssueFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                mIssueId
            )
        findNavController().navigate(
            action
        )
    }

    private fun noDataFound() {
        Toast.makeText(mContext, "No Data Found", Toast.LENGTH_SHORT).show()
    }

    private fun populateUi(issue: Issue) {

        issue_date_tv.text =
            issue.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

        val linearLayoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        complex_opinions_rv.layoutManager = linearLayoutManager
        complex_opinions_rv.setHasFixedSize(true)
            opinionsAdapter = FactsAdapter(
                mContext,
                this,
                mIssue.id.toString()
            )
        complex_opinions_rv.adapter = opinionsAdapter

        edit_iss_first_option_icon.setImageDrawable(
            mContext.getDrawable(mContext.resources.getIdentifier(
                issue.optionAIconName, "drawable", mContext.packageName
            ))
        )
        edit_iss_second_option_icon.setImageDrawable(
            mContext.getDrawable(mContext.resources.getIdentifier(
                issue.optionBIconName, "drawable", mContext.packageName
            ))
        )
        opinionsAdapter.setData(issue.opinions)
        binding.complexOpinionsRv.doOnPreDraw { startPostponedEnterTransition() }


    }

    private fun handleEditTitleClick() {
        //If the issue needs to be updated
        if (UiUtils.handleEditTitleClick(
                mContext,
                issue_title_tv,
                issue_title_et,
                edit_issue_title_icon
            )
        ) {
            if (mIssue != Issue.DEFAULT_ISSUE){
                mIssue.title = issue_title_et.text.toString()
                issue_title_tv.text = mIssue.title
                mViewModel.updateIssue(mIssue)
            }


        }
    }

    private fun handleToDecisionClick() {
        mIssue.toDecision().also {
            mViewModel.addDecision(it)
            mViewModel.updateIssue(mIssue)
            findNavController()
                .navigate(
                    EditIssueFragmentDirections.actionEditIssueFragmentToDecisionDetailsFragment(
                        it.id,
                        it.title
                    )
                )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun openOpinionScreen(opinion: Opinion, titleView: View) {
        val action =
            EditIssueFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(
                mIssue.id
            )
        action.opinionTitle = opinion.title
        val transitionName = mIssue.id.toString() + opinion.title
        titleView.transitionName = transitionName
        val fragmentNavigatorExtras = FragmentNavigatorExtras(
            titleView to transitionName
        )
        findNavController().navigate(
            action, fragmentNavigatorExtras
        )
    }

}
