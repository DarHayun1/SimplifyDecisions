package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_A_COLOR
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_B_COLOR
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.databinding.FragmentNewIssueTitleBinding
import dar.life.helpers.simplifydecisions.ui.UiUtils


class NewIssueTitleFragment : Fragment(), OnItemSelectedListener {

    private lateinit var mContext: Context
    private var _binding: FragmentNewIssueTitleBinding? = null
    private val binding get() = _binding!!

    private val mIssue: IssueModel? get() = viewModel.lastUsedIssue

    private val args: NewIssueTitleFragmentArgs by navArgs()


    private val viewModel by viewModels<IssuesViewModel>()

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
        _binding = FragmentNewIssueTitleBinding.inflate(layoutInflater, container,
            false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
    }

    private fun initView() {
        viewModel.lastUsedIssue = IssueModel.fromTemplate(args.template)
        initSpinners()
        binding.fromTemplateTitleLayout.editText?.apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            requestFocus()
            val imm =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            addTextChangedListener(onTextChanged = {text, _, _, _ ->
                mIssue?.title = text.toString()
            })
        }
        binding.firstOptionEt.apply {
            setText(mIssue!!.aTitle)
            addTextChangedListener(onTextChanged = {text, _, _, _ ->
                mIssue?.aTitle = text.toString()
            })
        }
        binding.secondOptionEt.apply {
            setText(mIssue!!.bTitle)
            addTextChangedListener(onTextChanged = {text, _, _, _ ->
                mIssue?.bTitle = text.toString()
            })
        }

        binding.newIssueNextFab.setOnClickListener{
            nextBtnRequest()

        }

    }

    private fun nextBtnRequest() {
        mIssue?.let { it ->
            val maxLength = binding.fromTemplateTitleLayout.counterMaxLength
            if (maxLength >= it.title.length) {
                viewModel.addNewIssue(it)
                findNavController().navigate(
                    NewIssueTitleFragmentDirections.actionNewIssueTitleFragmentToEditIssueFragment(
                        it.id, it.title
                    ).also { it.isNew = true }
                )
            }else
                Toast.makeText(mContext,
                    "Title length is limited to max $maxLength" +
                            " characters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSpinners() {
        val iconsList = UiUtils.getIconsList(mContext)
        binding.aIconsSpinner.adapter = IconsAdapter(mContext, iconsList)
        binding.aIconsSpinner.onItemSelectedListener = this

        binding.bIconsSpinner.adapter = IconsAdapter(mContext, iconsList)
        binding.bIconsSpinner.setSelection(1)
        binding.bIconsSpinner.onItemSelectedListener = this

    }

    /**
     * spinners interface
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent){
            binding.aIconsSpinner -> {mIssue?.aColorName =
                UiUtils.colorNames().getOrElse(position){ DEFAULT_A_COLOR }
            }
            binding.bIconsSpinner -> mIssue?.bColorName =
                UiUtils.colorNames().getOrElse(position){ DEFAULT_B_COLOR }
        }
    }

}
