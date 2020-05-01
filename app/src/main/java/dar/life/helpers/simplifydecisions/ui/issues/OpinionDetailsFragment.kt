package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentOpinionDetailsBinding
import dar.life.helpers.simplifydecisions.ui.UiUtils
import kotlinx.android.synthetic.main.fragment_opinion_details.*


/**
 * A simple [Fragment] subclass.
 */
class OpinionDetailsFragment : Fragment(), OnTaskTextChangedListener {

    private val mOpinion: Opinion? get() = viewModel.opinion
    private val mIssue: Issue? get() = viewModel.issue
    private var _binding: FragmentOpinionDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OpinionViewModel
    private lateinit var mContext: Context

    val args: OpinionDetailsFragmentArgs by navArgs()

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
        _binding = FragmentOpinionDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.opinionPos == -1)
            startWithTitleEdit()
        else{
            binding.opinionTitleTv.transitionName = args.issueId.toString() + args.opinionPos
            binding.opinionTitleTv.text = args.opinionTitle
        }

    }

    private fun startWithTitleEdit() {
        binding.opinionTitleTv.visibility = View.INVISIBLE
        binding.opinionTitleEt.visibility = View.VISIBLE
        binding.editOpinionTitleIcon.setImageDrawable(
            (mContext.getDrawable(R.drawable.confirm_edit_icon))
        )
        binding.opinionTitleEt.requestFocus()
        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(binding.opinionTitleEt, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(OpinionViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        viewModel.getIssueById(args.issueId).observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.issue = it
                issueFoundInflateFragment() } })

        binding.saveOpinionBtn.setOnClickListener{
            mIssue?.let { viewModel.updateIssue(mIssue!!) }
            findNavController().popBackStack()
        }

        binding.editOpinionTitleIcon.setOnClickListener {
            if (UiUtils.handleEditTitleClick(mContext,
                    binding.opinionTitleTv,
                    binding.opinionTitleEt,
                    binding.editOpinionTitleIcon)) {
                mOpinion?.title = binding.opinionTitleEt.text.toString()
                binding.opinionTitleTv.text = mOpinion?.title
            }
        }
    }


    private fun issueFoundInflateFragment() {
        viewModel.opinion = if (args.opinionPos == -1)
            Opinion(title = getString(R.string.default_opinion_title),
                certaintypercent = 80)
                .also {newOpinion -> mIssue!!.opinions.add(newOpinion)}
        else
            mIssue!!.opinions[args.opinionPos]
        Log.d("ABCD", mOpinion.toString())

        binding.relatedIssueTitle.text = mIssue?.title
        setupRadioGroup()
        setupTasks()
    }

    private fun setupTasks() {
        val adapter = TasksAdapter(mContext, this)
        binding.tasksRv.adapter = adapter
        binding.tasksRv.layoutManager = LinearLayoutManager(mContext, VERTICAL, false)
        Log.d("ABCD2", mOpinion.toString())
        adapter.tasks = mOpinion!!.tasks
        binding.addTaskBtn.setOnClickListener{
            mOpinion?.tasks?.let{
                it.add("")
                adapter.newTaskAdded(it)
            }
        }

    }

    private fun setupRadioGroup() {
        radioGroup.check(
            when (mOpinion!!.importance) {
                Opinion.LOW_IMPORTANCE -> R.id.radButton_low
                Opinion.MEDIUM_IMPORTANCE -> R.id.radButton_medium
                Opinion.HIGH_IMPORTANCE -> R.id.radButton_high
                //TODO: Game Changer
                else -> binding.radButtonHigh.id
            }
        )
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            mOpinion?.importance = when (checkedId) {
                R.id.radButton_low -> Opinion.LOW_IMPORTANCE
                R.id.radButton_medium -> Opinion.MEDIUM_IMPORTANCE
                else -> Opinion.HIGH_IMPORTANCE
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onTaskTextChange(pos: Int, text: String) {
        mOpinion?.tasks?.set(pos, text)
    }

}
