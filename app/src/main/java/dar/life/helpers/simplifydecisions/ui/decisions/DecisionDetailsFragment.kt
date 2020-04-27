package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs

import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.databinding.DecisionDetailsFragmentBinding
import kotlinx.android.synthetic.main.decision_details_fragment.*
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.FormatStyle

class DecisionDetailsFragment : Fragment() {

    private lateinit var mContext: Context
    private var mDecision: Decision? = null
    private var _binding: DecisionDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DecisionDetailsFragment()
    }

    private lateinit var viewModel: DecisionDetailsViewModel

    private val args: DecisionDetailsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DecisionDetailsFragmentBinding
            .inflate(inflater, container, true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)
            .get(DecisionDetailsViewModel::class.java)

        initViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.decisionTitleTv.transitionName = args.decisionId.toString()
        binding.decisionTitleTv.text = args.decisionTitle
    }

    private fun initViews() {
        viewModel.getDecisionById(args.decisionId)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    mDecision = it
                    populateUi(it)
                }
            })
    }

    private fun populateUi(decision: Decision) {
        binding.decisionDateTv.text = decision.date.format(ofLocalizedDate(FormatStyle.LONG))
        binding.decisionTitleTv.text =decision.title
        binding.editDecisionTitleIcon.setOnClickListener {
            if (decision_title_tv.visibility == View.VISIBLE) {
                decision_title_tv.visibility = View.INVISIBLE
                decision_title_et.visibility = View.VISIBLE
                decision_title_et.setText(decision_title_tv.text)
                edit_decision_title_icon.setImageDrawable(
                    mContext
                        .getDrawable(R.drawable.confirm_edit_icon)
                )
            } else {
                decision.title = decision_title_et.text.toString()
                decision_title_tv.text = decision.title
                viewModel.updateDecision(decision)
                decision_title_tv.visibility = View.VISIBLE
                decision_title_et.visibility = View.INVISIBLE
                edit_decision_title_icon.setImageDrawable(
                    (mContext
                        .getDrawable(R.drawable.pencil_edit_icon))
                )
                val imm: InputMethodManager? =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(
                    decision_title_et.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
            }
        }

    }

}
