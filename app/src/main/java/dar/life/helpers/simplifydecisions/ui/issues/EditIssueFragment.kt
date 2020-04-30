package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentEditIssueBinding
import dar.life.helpers.simplifydecisions.ui.UiUtils
import kotlinx.android.synthetic.main.facts_layout.*
import kotlinx.android.synthetic.main.fragment_edit_issue.*
import kotlinx.android.synthetic.main.fragment_issues.*
import kotlinx.android.synthetic.main.opinions_layout.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * A simple [Fragment] subclass.
 * Use the [EditIssueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditIssueFragment : Fragment(), OnOpinionRequest {
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
        sharedElementEnterTransition =
            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
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
        //TODO: fix return animation
        opinions_rv?.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun navigateToNewOpinion() {
        val action =
            EditIssueFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(mIssueId)
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

        val gridLayoutManager = GridLayoutManager(mContext, 2)
        opinions_rv.layoutManager = gridLayoutManager
        opinions_rv.setHasFixedSize(true)
        val opinionsAdapter = OpinionsAdapter(mContext, this)
        opinions_rv.adapter = opinionsAdapter

        opinionsAdapter.setData(issue.opinions.filter { !it.isAFact })


        val (positive, negative) = issue.opinions.filter { it.isAFact }
            .partition { it.isPositive }
        populateFacts(true, positive)
        populateFacts(false, negative)

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
                .navigate(EditIssueFragmentDirections
                .actionEditIssueFragmentToDecisionDetailsFragment(it.id, it.title))
        }
    }

    private fun populateFacts(
        isPositive: Boolean,
        factsList: List<Opinion>
    ) {
        val adapter = FactsAdapter(mContext, isPositive)
        val recyclerView = if (isPositive) facts_positive_rv else facts_negative_rv
        recyclerView.layoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        recyclerView.adapter = adapter
        adapter.mFacts = factsList.sortedBy { it.importance }.reversed()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun openOpinionScreen(position: Int, title: String, titleView: View) {
        val action =
            EditIssueFragmentDirections.actionEditIssueFragmentToOpinionDetailsFragment(mIssue.id)
        action.opinionPos = position
        action.opinionTitle = title
        val transitionName = mIssue.id.toString() + position
        titleView.transitionName = transitionName
        val fragmentNavigatorExtras = FragmentNavigatorExtras(
            titleView to transitionName
        )
        findNavController().navigate(
            action, fragmentNavigatorExtras
        )
    }

}
