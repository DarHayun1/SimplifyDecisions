package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentEditIssueBinding
import kotlinx.android.synthetic.main.facts_layout.*
import kotlinx.android.synthetic.main.fragment_edit_issue.*
import kotlinx.android.synthetic.main.opinions_layout.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * A simple [Fragment] subclass.
 * Use the [EditIssueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditIssueFragment : Fragment() {
    private lateinit var mContext: Context
    private lateinit var mViewModel: EditIssueViewModel

    // TODO: Rename and change types of parameters
    private var mIssueId: Int = 0
    private lateinit var mIssue: Issue

    private var _binding: FragmentEditIssueBinding? = null
    private val binding get() = _binding!!

    val args: EditIssueFragmentArgs by navArgs()

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

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.issueTitleTv.transitionName = args.issueId.toString()
        binding.issueTitleTv.text = args.issueTitle

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EditIssueViewModel::class.java)

        mViewModel.getIssueById(args.issueId.toString())?.observe(viewLifecycleOwner, Observer {
            it?.run {
                mIssue = this
                populateUi()
            }
        })

    }

    private fun noDataFound() {
        Toast.makeText(mContext, "No Data Found", Toast.LENGTH_SHORT).show()
    }

    private fun populateUi() {
        issue_date_tv.text = mIssue.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

        var gridLayoutManager = GridLayoutManager(mContext, 2)
        opinions_rv.layoutManager = gridLayoutManager
        opinions_rv.setHasFixedSize(true)
        val opinionsAdapter = OpinionsAdapter(mContext)
        opinions_rv.adapter = opinionsAdapter
        opinionsAdapter.setData(mIssue.opinions.filter { !it.isAFact() })


        val (positive, negative) = mIssue.opinions.filter { it.isAFact() }.partition { it.isPositive }
        populateFacts(true, positive)
        populateFacts(false, negative)

        to_decision_button.setOnClickListener {
            val decision = mIssue.toDecision().also {
                mViewModel.addDecision(it)
            }
            mViewModel.updateIssue(mIssue)
            Toast.makeText(mContext, decision.toString(), Toast.LENGTH_SHORT).show()
        }

        edit_issue_title_icon.setOnClickListener {
            if (issue_title_tv.visibility == View.VISIBLE) {
                issue_title_tv.visibility = View.INVISIBLE
                issue_title_et.visibility = View.VISIBLE
                issue_title_et.setText(issue_title_tv.text)
                edit_issue_title_icon.setImageDrawable(
                    mContext
                        .getDrawable(R.drawable.confirm_edit_icon)
                )
            } else {
                mIssue.title = issue_title_et.text.toString()
                issue_title_tv.text = mIssue.title
                mViewModel.updateIssue(mIssue)
                issue_title_tv.visibility = View.VISIBLE
                issue_title_et.visibility = View.INVISIBLE
                edit_issue_title_icon.setImageDrawable(
                    (mContext
                        .getDrawable(R.drawable.pencil_edit_icon))
                )
                val imm: InputMethodManager? =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(
                    issue_title_et.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
            }
        }
    }

    private fun populateFacts(
        isPositive: Boolean,
        factsList: List<Opinion>) {
        val adapter = FactsAdapter(mContext, isPositive)
        val recyclerView = if (isPositive) facts_positive_rv else facts_negative_rv
        recyclerView.layoutManager = LinearLayoutManager(mContext,
            RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.mFacts = factsList.sortedBy { it.importance }.reversed()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
