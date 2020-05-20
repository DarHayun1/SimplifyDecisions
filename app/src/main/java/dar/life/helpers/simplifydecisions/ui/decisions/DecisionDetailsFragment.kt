package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout

import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Goal
import dar.life.helpers.simplifydecisions.databinding.DecisionDetailsFragmentBinding
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.FormatStyle

class DecisionDetailsFragment : Fragment(), OnGoalClickListener {

    private var isNewDecision: Boolean = false
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DecisionDetailsFragmentBinding.inflate(inflater, container, false)
        Log.d("TEST", "onCreateView")
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.decision_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)
            .get(DecisionDetailsViewModel::class.java)
        initToolbar()
        initViews()
    }


    private fun initToolbar() {
        binding.decisionDetailsToolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.decisionDetailsToolbar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.decisionId == -1)
        {
            isNewDecision = true
        }
        binding.decisionDetailsToolbarTitle.transitionName = args.decisionId.toString()
        binding.decisionDetailsToolbarTitle.text = args.decisionTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_decision_title -> {
                editDecisionTitle()
                true
            }
            R.id.action_collaborate_decision -> {
                handleCollaborateClick()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun initViews() {
        viewModel.getDecisionById(args.decisionId)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    mDecision = it
                    populateUi(it)
                }
            })
        setupGoals()
    }

    private fun setupGoals() {
        val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.goalsRv.adapter = GoalsAdapter(mContext, this)
        binding.goalsRv.layoutManager = layoutManager

        binding.addAGoal.setOnClickListener{onNewGoalRequest()}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleCollaborateClick() {
        Toast.makeText(mContext, "Upcoming feature (: ", Toast.LENGTH_SHORT).show()
    }

    private fun editDecisionTitle() {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.edit_title_layout, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.text_input_layout)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        textInputLayout.editText?.setText(mDecision?.title)
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength){
                mDecision?.title = textInputLayout.editText?.text.toString()
                binding.decisionDetailsToolbarTitle.text = mDecision?.title
                dialogBuilder.dismiss()
            }else
                Toast.makeText(mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT).show()

        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun populateUi(decision: Decision) {
        binding.decisionDateTv.text = decision.date.format(ofLocalizedDate(FormatStyle.LONG))
        binding.decisionDetailsToolbarTitle.text = decision.title
        (binding.goalsRv.adapter as GoalsAdapter).goalsList = decision.goals
    }

    override fun onNewGoalRequest() {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.new_goal_layout, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.goal_edit_text_input_layout)
        val cancelBtn: Button = dialogView.findViewById(R.id.edit_goal_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.edit_goal_save_button)

        textInputLayout.editText?.setText(mDecision?.title)
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mDecision?.goals?.add(Goal(textInputLayout.editText?.text.toString()))
                binding.goalsRv.adapter?.notifyDataSetChanged()
                dialogBuilder.dismiss()
            }else{
                Toast.makeText(mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

}
