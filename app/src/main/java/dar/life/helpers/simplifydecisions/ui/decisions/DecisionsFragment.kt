package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.DecisionModel
import dar.life.helpers.simplifydecisions.databinding.FragmentDecisionsBinding
import dar.life.helpers.simplifydecisions.ui.Instruction
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import kotlinx.android.synthetic.main.fragment_decisions.*

/**
 * In charge of displaying the list of decisions
 */
class DecisionsFragment : Fragment(), OnDetailsRequest {

    private lateinit var mShowcaseView: ShowcaseView

    private val mViewModel by viewModels<DecisionsViewModel>()
    private lateinit var mContext: Context
    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }

    private fun backPressed() {
        if (!hideHelpIfShown())
        {
            clearCallback()
            findNavController().popBackStack()
        }
    }

    private fun clearCallback() {
        mBackPressedCallback.remove()
    }

    private var _binding: FragmentDecisionsBinding? = null
    private val binding get() = _binding!!

    // ****************************
    // ***** Fragment methods *****
    // ****************************

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

        initViews()
        requireActivity().onBackPressedDispatcher
            .addCallback(mBackPressedCallback)

    }

    override fun onDestroyView() {
        hideHelpIfShown()
        _binding = null
        clearCallback()
        super.onDestroyView()
    }

    // ****************************
    // ***** Helper methods *****
    // ****************************

    private fun initViews() {

        val rvAdapter = DecisionsAdapter(mContext, this)
        binding.decisionsRv.adapter = rvAdapter
        (binding.decisionsRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.decisionsRv.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.decisionsRv.addItemDecoration(
            DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
        )

        mViewModel.getAllDecisions().observe(viewLifecycleOwner, Observer {
            rvAdapter.decisions = it
            if (it.isEmpty() && !mViewModel.gotDecisionsFirstHelp)
                startInstructions()
        })
        binding.addDecisionFab.setOnClickListener {
            newDecisionRequest()
        }
        postponeEnterTransition()
        decisions_rv.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun newDecisionRequest() {
        hideHelpIfShown()
        clearCallback()
        mViewModel.addNewDecision(DecisionModel(
            getString(R.string.new_decision_title),
            null))
        val fragmentNavExtras = FragmentNavigatorExtras(
            (
                    binding.decisionsBottomDrawer to getString((R.string.bot_drawer_trans_name)))
        )
        val action = DecisionsFragmentDirections
            .actionDecisionsFragmentToDecisionDetailsFragment(
                mViewModel.lastUsedDecision!!.id,
                getString(R.string.new_decision_title)
            )
        action.isNew = true
        findNavController().navigate(
            action,
            fragmentNavExtras
        )
    }



    private fun hideHelpIfShown(): Boolean {
        if (isHelpMode()) {
            mShowcaseView.hide()
            return true
        }
        return false
    }

    private fun isHelpMode() = this::mShowcaseView.isInitialized && mShowcaseView.isShown

    private fun startInstructions() {
        val instruction = getInstruction()
        mShowcaseView = ShowcaseView.Builder(activity)
            .withHoloShowcase()
            .hideOnTouchOutside()
            .setStyle(R.style.ShowcaseTheme)
            .setTarget(ViewTarget(instruction.view))
            .setContentTitle(instruction.title)
            .setContentText(instruction.text.trimMargin())
            .build()
        mShowcaseView.hideButton()
    }

    private fun getInstruction(): Instruction = Instruction(
        getString(R.string.first_decision_title),
        getString(R.string.first_decision_text),
        binding.addDecisionFab)

    // **************************
    // ***** Click handlers *****
    // **************************

    override fun openDetailsScreen(id: Int, title: String, titleView: View) {

        val fragmentNavigatorExtras =
            FragmentNavigatorExtras(
                titleView to id.toString(),
                binding.decisionsBottomDrawer to getString(R.string.bot_drawer_trans_name)
            )
        findNavController().navigate(
            DecisionsFragmentDirections
                .actionDecisionsFragmentToDecisionDetailsFragment(id, title),
            fragmentNavigatorExtras
        )
    }
}

