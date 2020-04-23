package dar.life.helpers.simplifydecisions.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.databinding.DashboardFragmentBinding
import dar.life.helpers.simplifydecisions.databinding.FragmentIssuesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IssuesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IssuesFragment : Fragment() {

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

    val newIssueDeclaration = {_: View ->
        // newIssueFragment
    }

    private fun initViews() {
        binding.addIssueFab.setOnClickListener(newIssueDeclaration)
        val issuesAdapter = IssuesAdapter(mContext)
        mViewModel.getAllIssues().observe(viewLifecycleOwner, Observer
            {issues: List<Issue> -> issuesAdapter.setData(issues)})
    }

}
