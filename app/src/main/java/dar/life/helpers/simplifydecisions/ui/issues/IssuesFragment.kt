package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.databinding.FragmentIssuesBinding
import kotlinx.android.synthetic.main.fragment_issues.*
import kotlinx.android.synthetic.main.issues_list_item.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [IssuesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IssuesFragment : Fragment(), OnIssueEditClick {

    private lateinit var mViewModel: IssuesViewModel
    private lateinit var mContext: Context
    private var _binding: FragmentIssuesBinding? = null
    private val binding get() = _binding!!

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment IssuesFragment.
         */
        @JvmStatic
        fun newInstance() =
            IssuesFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIssuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(IssuesViewModel::class.java)
        initViews()
    }


    private fun initViews() {
        binding.addIssueFab.setOnClickListener{
            val issueId = mViewModel.addNewIssue().id
            Log.d("HEYHEY", issueId)
            findNavController().navigate(
                IssuesFragmentDirections.actionIssuesFragmentToEditIssueFragment(
                    issueId,
                    mContext.getString(R.string.new_issue_title)
                )
            )
        }
        setupIssuesList()
    }

    private fun setupIssuesList() {
        val issuesAdapter =
            IssuesAdapter(mContext, this)

        issues_rv.adapter = issuesAdapter
        (issues_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        issues_rv.layoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        issues_rv.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        mViewModel.getAllActiveIssues().observe(viewLifecycleOwner, Observer
        { issuesAdapter.setData(it) }
        )
        postponeEnterTransition()
        issues_rv.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun openIssueDetails(issueId: String, issueTitle: String, view: View) {
        val fragmentNavigatorExtras = FragmentNavigatorExtras(
            view to issueId
        )
        findNavController().navigate(
            IssuesFragmentDirections.actionIssuesFragmentToEditIssueFragment(issueId, issueTitle),
            fragmentNavigatorExtras
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
