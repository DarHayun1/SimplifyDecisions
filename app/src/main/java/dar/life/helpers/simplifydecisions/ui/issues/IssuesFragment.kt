package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
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
import dar.life.helpers.simplifydecisions.databinding.FragmentIssuesBinding
import dar.life.helpers.simplifydecisions.ui.Instruction
import dar.life.helpers.simplifydecisions.ui.OnDetailsRequest
import kotlinx.android.synthetic.main.fragment_issues.*

/**
 * A [Fragment] In charge of displaying the [IssueModel]s list
 */
class IssuesFragment : Fragment(),
    OnDetailsRequest {

    private lateinit var mShowcaseView: ShowcaseView
    private val mViewModel by viewModels<IssuesViewModel>()
    private lateinit var mContext: Context

    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }

    private fun backPressed() {
        if (!hideHelpIfShown()) {
            clearCallback()
            findNavController().popBackStack()
        }
    }

    private fun clearCallback() {
        mBackPressedCallback.remove()
    }

    private var _binding: FragmentIssuesBinding? = null
    private val binding get() = _binding!!


    // *****************************
    // ***** Fragment methods *****
    // *****************************

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
        _binding = FragmentIssuesBinding.inflate(inflater, container, false)
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
        super.onDestroyView()
    }


    // ****************************
    // ***** Helper methods *****
    // ****************************

    private fun initViews() {
        binding.addIssueFab.setOnClickListener {
            newIssueClicked()
        }
        setupIssuesList()
    }

    private fun newIssueClicked() {
        hideHelpIfShown()
        clearCallback()
        findNavController().navigate(
            IssuesFragmentDirections.actionIssuesFragmentToCreateFromTemplateFragment()
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

    private fun setupIssuesList() {
        val issuesAdapter =
            IssuesAdapter(
                mContext,
                this
            )

        issues_rv.adapter = issuesAdapter
        (issues_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        issues_rv.layoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        issues_rv.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        mViewModel.getAllActiveIssues().observe(viewLifecycleOwner, Observer
        {
            if (it.isEmpty())
                mViewModel.getAllIssues().observe(viewLifecycleOwner, Observer { allIssues ->
                    if (allIssues.isEmpty())
                        startInstructions()
                })
            issuesAdapter.issues = it
        }
        )
        postponeEnterTransition()
        issues_rv.doOnPreDraw { startPostponedEnterTransition() }
    }

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
        getString(R.string.first_dilemma_title),
        getString(R.string.first_dilemma_text),
        binding.addIssueFab
    )


    override fun openDetailsScreen(id: Int, title: String, titleView: View) {
        val extras = FragmentNavigatorExtras(
            titleView to id.toString(),
            binding.issuesBottomDrawer to getString(R.string.bot_drawer_trans_name)
        )
        hideHelpIfShown()
        clearCallback()
        findNavController().navigate(
            IssuesFragmentDirections.actionIssuesFragmentToEditIssueFragment(
                id,
                title
            ),
            extras
        )
    }


}
