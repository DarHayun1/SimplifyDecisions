package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.databinding.FragmentDecisionsBinding
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import kotlinx.android.synthetic.main.fragment_decisions.*
import kotlinx.android.synthetic.main.fragment_issues.*

/**
 * A simple [Fragment] subclass.
 * Use the [DecisionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DecisionsFragment : Fragment(), OnDetailsRequest {

    private lateinit var mDecisionsViewModel: DecisionsViewModel
    private lateinit var mContext: Context
    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }

    private fun backPressed() {

        mBackPressedCallback.remove()
    }

    private var _binding: FragmentDecisionsBinding? = null
    private val binding get() = _binding!!

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DecisionsFragment.
         */
        @JvmStatic
        fun newInstance() =
            DecisionsFragment()
    }

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
        // Inflate the layout for this fragment
        _binding = FragmentDecisionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDecisionsViewModel = ViewModelProvider(this).get(DecisionsViewModel::class.java)

        initViews()
//        requireActivity().onBackPressedDispatcher
//            .addCallback(mBackPressedCallback)

    }

    private fun initViews() {

        val rvAdapter = DecisionsAdapter(mContext, this)
        binding.decisionsRv.adapter = rvAdapter
        (binding.decisionsRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.decisionsRv.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.decisionsRv.addItemDecoration(
            DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
        )

        mDecisionsViewModel.getAllDecisions().observe(viewLifecycleOwner, Observer {
            rvAdapter.decisions = it
        })
        binding.addDecisionFab.setOnClickListener {
            newDecisionRequest()
        }
        postponeEnterTransition()
        decisions_rv.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun newDecisionRequest() {
        mDecisionsViewModel.addNewDecision(Decision(
            getString(R.string.new_decision_title),
            null))
        val fragmentNavExtras = FragmentNavigatorExtras(
            (
                    binding.decisionsBottomDrawer to getString((R.string.bot_drawer_trans_name)))
        )
        val action = DecisionsFragmentDirections
            .actionDecisionsFragmentToDecisionDetailsFragment(
                mDecisionsViewModel.lastUsedDecision!!.id,
                getString(R.string.new_decision_title)
            )
        action.isNew = true
        findNavController().navigate(
            action,
            fragmentNavExtras
        )
    }

    override fun openDetailsScreen(id: Int, title: String, view: View) {

        val fragmentNavigatorExtras =
            FragmentNavigatorExtras(
                view to id.toString(),
                binding.decisionsBottomDrawer to getString(R.string.bot_drawer_trans_name)
            )
        findNavController().navigate(
            DecisionsFragmentDirections
                .actionDecisionsFragmentToDecisionDetailsFragment(id, title),
            fragmentNavigatorExtras
        )
    }
}

