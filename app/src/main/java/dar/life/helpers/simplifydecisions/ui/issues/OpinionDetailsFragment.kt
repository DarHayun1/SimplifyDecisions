package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.Constants.Companion.DEFAULT_CATEGORY
import dar.life.helpers.simplifydecisions.Constants.Companion.NEW_CATEGORY
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.data.Opinion.Task
import dar.life.helpers.simplifydecisions.databinding.FragmentOpinionDetailsBinding
import kotlinx.android.synthetic.main.fragment_opinion_details.*


/**
 * A simple [Fragment] subclass.
 */
class OpinionDetailsFragment : Fragment(),
    OnTaskTextChangedListener {

    private var isNewOpinion: Boolean = false
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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.opinion_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.opinionTitle == "New Opinion") {
            isNewOpinion = true
            setupTopDrawer()
        }
        else{
            val transitionName = args.issueId.toString() + args.opinionTitle
            binding.opinionDetailsToolbarTitle.transitionName = transitionName
            binding.opinionDetailsToolbarTitle.text = args.opinionTitle

            binding.topDrawerOpinionDetails.transitionName = transitionName +
                    getString(R.string.frame_transition_name_extension)
            binding.topDrawerOpinionDetails.setBackgroundColor(args.opinionColor)
            Handler().postDelayed({
                setupTopDrawer()
            }, 300)


        }

    }

    private fun setupTopDrawer() {
        binding.topDrawerOpinionDetails.elevation = 0f
        binding.root.setBackgroundColor(args.opinionColor)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val slide = Slide(Gravity.BOTTOM)
        slide.addTarget(binding.opinionDetailsBottomDrawer)
        slide.interpolator = AnimationUtils.loadInterpolator(
            mContext,
            android.R.interpolator.linear_out_slow_in
        )
        requireActivity().window.enterTransition = slide
        viewModel = ViewModelProvider(this).get(IssuesViewModel::class.java)
        initToolbar()
        initViews()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_opinion_title -> editTitle()
            R.id.action_save_opinion -> saveClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editTitle() {
        val alertDialog: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.edit_title_layout, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.text_input_layout)
        val dialogTitle: TextView = dialogView.findViewById(R.id.edit_title_header_tv)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        dialogTitle.text = getString(R.string.edit_opinion_title_label)
        textInputLayout.hint = getString(R.string.opinion_title)
        mOpinion?.let {
            textInputLayout.editText?.setText(mOpinion?.title)
        }

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mOpinion?.title = textInputLayout.editText?.text.toString()
                binding.opinionDetailsToolbarTitle.text = mOpinion?.title
                alertDialog.dismiss()
            }else
                Toast.makeText(mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT).show()
        }
        alertDialog.setView(dialogView)
        alertDialog.show()

        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(binding.opinionDetailsToolbarTitle, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun initViews() {
        viewModel.getIssueById(args.issueId).observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.lastUsedIssue = it
                issueFoundInflateFragment(it) } })

        if(isNewOpinion) editTitle()
    }

    private fun saveClicked() {
        mIssue?.let {
            if (category == "")
                category = DEFAULT_CATEGORY
            it.changeOpinionCategory(mOpinion!!, category)
            viewModel.updateIssue(mIssue!!)
        }
        findNavController().popBackStack()
    }

    private fun issueFoundInflateFragment(issue: Issue) {

        binding.relatedIssueTitle.text = issue.displayedTitle(mContext)

        viewModel.lastUsedOpinion =
            issue.opinions.values.flatten().firstOrNull { it.title == args.opinionTitle }
        if (mOpinion == null)
            viewModel.lastUsedOpinion = Opinion(getString(R.string.default_opinion_title),
                isOfFirstOption = args.ofFirstOption)
                .also {newOpinion -> mIssue!!.opinions[DEFAULT_CATEGORY]!!.add(newOpinion)}

        binding.opinionDetailsToolbarTitle.text = mOpinion?.title
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

    private fun initToolbar() {
        binding.editOpinionToolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.editOpinionToolbar)
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

    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        view.startAnimation(animate)
    }

    override fun onTaskTextChange(pos: Int, text: String) {
        mOpinion?.tasks?.set(pos, Task(text))
    }

    override fun onCheckedChanged(pos: Int, checked: Boolean) {
        mOpinion?.checkTask(pos, checked)

    }

}
