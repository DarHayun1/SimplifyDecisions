package dar.life.helpers.simplifydecisions.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dar.life.helpers.simplifydecisions.Constants.REMINDER_ID_KEY
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.databinding.DashboardFragmentBinding

class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    companion object {
        fun newInstance() =
            DashboardFragment()
    }

    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding!!


    private val mViewModel by viewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.issuesBtn.setOnClickListener {
            openIssues()
        }
        binding.decisionsBtn.setOnClickListener {
            openDecisions()
        }
    }

    private fun openIssues() {
        val extras = FragmentNavigatorExtras(
            binding.dashboardLogo to getString(R.string.dash_to_issues_logo_trans),
            binding.dashboardBackground to getString(R.string.bot_drawer_trans_name)
        )
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardFragmentToIssuesFragment(),
            extras
        )
    }

    private fun openDecisions() {
        val extras = FragmentNavigatorExtras(
            binding.dashboardLogo to getString(R.string.dash_to_issues_logo_trans),
            binding.dashboardBackground to getString(R.string.bot_drawer_trans_name)
        )
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardFragmentToDecisionsFragment(), extras
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val intent = requireActivity().intent
        if (intent.hasExtra(REMINDER_ID_KEY)) {
            navigateToRemindedDecision(intent.getLongExtra(REMINDER_ID_KEY, -1))
            intent.removeExtra(REMINDER_ID_KEY)
        }

    }

    private fun navigateToRemindedDecision(reminderId: Long) {

        mViewModel.getAllDecisions().observe(viewLifecycleOwner, Observer { decisions ->
            val decision = decisions.find { decision ->
                decision.goals.map { goal ->
                    goal.reminder
                }.any { reminder -> reminder.id == reminderId }
            }

            decision?.let {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToDecisionDetailsFragment(
                        decision.id, decision.title
                    )
                action.reminderId = reminderId
                val extras = FragmentNavigatorExtras(
                    binding.dashboardBackground to getString(R.string.bot_drawer_trans_name)
                )
                findNavController().navigate(action, extras)
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
