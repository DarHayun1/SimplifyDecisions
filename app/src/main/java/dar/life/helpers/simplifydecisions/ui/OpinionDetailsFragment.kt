package dar.life.helpers.simplifydecisions.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import dar.life.helpers.simplifydecisions.Constants.Companion.DEFAULT_CATEGORY
import dar.life.helpers.simplifydecisions.Constants.Companion.NEW_CATEGORY
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.data.Opinion.Task
import dar.life.helpers.simplifydecisions.databinding.FragmentOpinionDetailsBinding
import dar.life.helpers.simplifydecisions.ui.issues.IssuesViewModel
import dar.life.helpers.simplifydecisions.ui.issues.OnTaskTextChangedListener
import dar.life.helpers.simplifydecisions.ui.issues.TasksAdapter
import kotlinx.android.synthetic.main.fragment_opinion_details.*


/**
 * A simple [Fragment] subclass.
 */
class OpinionDetailsFragment : Fragment(),
    OnTaskTextChangedListener {

    private lateinit var mTasksAdapter: TasksAdapter
    private val mOpinion: Opinion? get() = viewModel.lastUsedOpinion
    private val mIssue: Issue? get() = viewModel.lastUsedIssue
    private var category: String = DEFAULT_CATEGORY
    private var _binding: FragmentOpinionDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: IssuesViewModel
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

        if(args.opinionTitle == "New Opinion")
            startWithTitleEdit()
        else{//TODO: new UNIQUE transition names
            binding.opinionTitleTv.transitionName = args.issueId.toString() + args.opinionTitle
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

        viewModel = ViewModelProvider(this).get(IssuesViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        viewModel.getIssueById(args.issueId).observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.lastUsedIssue = it
                issueFoundInflateFragment() } })

        saveBtnInit()

        editTitleViewInit()

        binding.ofFirstCb.setOnCheckedChangeListener {
                _, isChecked ->
            mOpinion?.isOfFirstOption = isChecked
        }
    }

    private fun editTitleViewInit() {
        binding.editOpinionTitleIcon.setOnClickListener {
            if (UiUtils.handleEditTitleClick(
                    mContext,
                    binding.opinionTitleTv,
                    binding.opinionTitleEt,
                    binding.editOpinionTitleIcon
                )
            ) {
                mOpinion?.title = binding.opinionTitleEt.text.toString()
                binding.opinionTitleTv.text = mOpinion?.title
            }
        }
    }

    private fun saveBtnInit() {
        binding.saveOpinionBtn.setOnClickListener {
            mIssue?.let {
                it.changeOpinionCategory(mOpinion!!, category)
                viewModel.updateIssue(mIssue!!) }
            findNavController().popBackStack()
        }
    }


    private fun issueFoundInflateFragment() {
        viewModel.lastUsedOpinion =
            mIssue!!.opinions.values.flatten().firstOrNull { it.title == args.opinionTitle }
        if (mOpinion == null)
            viewModel.lastUsedOpinion = Opinion(getString(R.string.default_opinion_title))
                .also {newOpinion -> mIssue!!.opinions[DEFAULT_CATEGORY]!!.add(newOpinion)}

        binding.relatedIssueTitle.text = mIssue?.title
        setupRadioGroup()
        setupTasks()

        initCategorySpinner()
    }

    private fun initCategorySpinner() {
        binding.categorySpinner.adapter =
            ArrayAdapter(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                mIssue!!.opinions.keys.toMutableList().also { it.add(NEW_CATEGORY) })
        binding.categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tempCategory = parent.getItemAtPosition(position) as String
                category = if (tempCategory == NEW_CATEGORY) {
                    binding.newCategoryEt.visibility = View.VISIBLE
                    binding.newCategoryEt.text.toString()
                }
                else {
                    binding.newCategoryEt.visibility = View.INVISIBLE
                    tempCategory
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.newCategoryEt.addTextChangedListener(onTextChanged = {
            text, _, _, _ ->
            category = text.toString()
        })
    }

    private fun setupTasks() {
        mTasksAdapter =
            TasksAdapter(
                mContext,
                this
            )
        binding.tasksRv.adapter = mTasksAdapter
        binding.tasksRv.layoutManager = LinearLayoutManager(mContext, VERTICAL, false)
        Log.d("ABCD2", mOpinion.toString())
        mTasksAdapter.setData(mOpinion!!.tasks)
        binding.addTaskBtn.setOnClickListener{
            mOpinion?.tasks?.let{
                it.add(Task(""))
                mTasksAdapter.newTaskAdded(it)
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
        mOpinion?.tasks?.set(pos, Task(text))
    }

    override fun onCheckedChanged(pos: Int, checked: Boolean) {
        mOpinion?.checkTask(pos, checked)

    }

}
