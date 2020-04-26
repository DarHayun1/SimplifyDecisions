package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import dar.life.helpers.simplifydecisions.data.Decision
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditIssueBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EditIssueViewModel::class.java)
        val issueLiveData: LiveData<Issue>? =
            mViewModel.getIssueById(args.issueId)
        if (issueLiveData != null)
            issueLiveData.observe(viewLifecycleOwner, Observer {
                mIssue = it
                populateUi()
            })
        else
            noDataFound()

    }

    private fun noDataFound() {
        TODO("IMPLEMENT ME PLEASE!")
    }

    private fun populateUi() {
        issue_title_tv.text = mIssue.title
        issue_date_tv.text = mIssue.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

        var gridLayoutManager = GridLayoutManager(mContext, 2)
        opinions_rv.layoutManager = gridLayoutManager
        opinions_rv.setHasFixedSize(true)
        val opinionsAdapter = OpinionsAdapter(mContext)
        opinions_rv.adapter = opinionsAdapter
        opinionsAdapter.setData(mIssue.opinions.filter { !it.isAFact() })

        val (positive, negative) = mIssue.opinions.filter { it.isAFact() }.partition { it.isPositive }
        pupulateFacts(true, positive)
        pupulateFacts(false, negative)

        to_decision_button.setOnClickListener(View.OnClickListener {
            val decision: Decision = mIssue.toDecision()
            Toast.makeText(mContext, decision.toString(), Toast.LENGTH_SHORT).show()
        })


    }

    private fun pupulateFacts(
        isPositive: Boolean,
        factsList: List<Opinion>) {
        val adapter = FactsAdapter(mContext, isPositive)
        val recyclerView = if (isPositive) facts_positive_rv else facts_negative_rv
        recyclerView.layoutManager = LinearLayoutManager(mContext,
            RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.mFacts = factsList.sortedBy { it.importance }.reversed()
    }


}
