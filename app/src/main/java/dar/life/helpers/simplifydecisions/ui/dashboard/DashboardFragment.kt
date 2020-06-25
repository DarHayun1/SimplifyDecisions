package dar.life.helpers.simplifydecisions.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dar.life.helpers.simplifydecisions.Constants.REMINDER_ID_KEY
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.databinding.DashboardFragmentBinding

/**
 * First App screen, the main menu (Issues/Decisions)
 *
 */
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val intent = requireActivity().intent
        if (intent.hasExtra(REMINDER_ID_KEY)) {
            navigateToRemindedDecision(intent.getLongExtra(REMINDER_ID_KEY, -1))
            intent.removeExtra(REMINDER_ID_KEY)
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViews() {
        binding.issuesBtn.setOnClickListener {
            openIssues()
        }
        binding.decisionsBtn.setOnClickListener {
            openDecisions()
        }
    }

    // *************************
    // **** Click Listeners ****
    // *************************

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

    // **** End of Click Listeners


    /**
     * Gets called when the user entered the app through a reminder.
     * navigating to the corresponding decision.
     *
     * @param reminderId
     */
    private fun navigateToRemindedDecision(reminderId: Long) {

        //Searching for the reminder with the given id
        mViewModel.getAllDecisions().observe(viewLifecycleOwner, Observer { decisions ->
            val decision = decisions.find { decision ->
                decision.goals.map { goal ->
                    goal.reminder
                }.any { reminder -> reminder.id == reminderId }
            }

            //navigating to the decision, if found.
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

}
