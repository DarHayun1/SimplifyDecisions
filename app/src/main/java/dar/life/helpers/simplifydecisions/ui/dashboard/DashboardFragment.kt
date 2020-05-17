package dar.life.helpers.simplifydecisions.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.databinding.DashboardFragmentBinding

class DashboardFragment : Fragment(R.layout.dashboard_fragment) {

    companion object {
        fun newInstance() =
            DashboardFragment()
    }
    private var _binding: DashboardFragmentBinding? = null
    private val binding get() = _binding!!


    private lateinit var mViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.issuesBtn.setOnClickListener{
            val extras = FragmentNavigatorExtras(
                binding.dashboardLogo to getString(R.string.dash_to_issues_logo_trans),
                binding.dashboardBackground to getString(R.string.bot_drawer_trans_name)
            )
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToIssuesFragment(),
                extras
            )
        }
        binding.decisionsBtn.setOnClickListener{
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToDecisionsFragment()
        )}
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
