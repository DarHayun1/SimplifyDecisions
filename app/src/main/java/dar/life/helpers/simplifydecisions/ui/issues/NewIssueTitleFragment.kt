package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dar.life.helpers.simplifydecisions.Constants.Companion.DEFAULT_A_ICON
import dar.life.helpers.simplifydecisions.Constants.Companion.DEFAULT_B_ICON
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.databinding.FragmentNewIssueTitleBinding


class NewIssueTitleFragment : Fragment() {

    private lateinit var mContext: Context
    private var _binding: FragmentNewIssueTitleBinding? = null
    private val binding get() = _binding!!

    private val mIssue: Issue? get() = viewModel.lastUsedIssue

    private val args: NewIssueTitleFragmentArgs by navArgs()


    private lateinit var viewModel: IssuesViewModel

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
        viewModel = ViewModelProvider(requireActivity()).get(IssuesViewModel::class.java)

        initView()
    }

    private fun initView() {
        viewModel.lastUsedIssue = Issue.fromTemplate(args.template)
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
            setText(mIssue!!.optionAName)
            addTextChangedListener(onTextChanged = {text, _, _, _ ->
                mIssue?.optionAName = text.toString()
            })
        }
        binding.secondOptionEt.apply {
            setText(mIssue!!.optionBName)
            addTextChangedListener(onTextChanged = {text, _, _, _ ->
                mIssue?.optionBName = text.toString()
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
                    )
                )
            }else
                Toast.makeText(mContext,
                    "Title length is limited to max $maxLength" +
                            " characters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSpinners() {
        val iconsList = getIconsList()
        binding.spinner1.adapter = IconsAdapter(mContext, iconsList)
        binding.spinner2.adapter = IconsAdapter(mContext, iconsList)
        binding.spinner2.setSelection(1)

        binding.spinner1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                mIssue?.optionAIconName = getIconsNames()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinner2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                mIssue?.optionBIconName = getIconsNames()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getIconsList(): MutableList<Drawable> {
        return getIconsNames().map {
            mContext.getDrawable(
                mContext.resources.getIdentifier(it, "drawable", mContext.packageName)
            )!!
        }.toMutableList()
    }
    private fun getIconsNames(): List<String>{
        return listOf(
            DEFAULT_A_ICON,
            DEFAULT_B_ICON,
            "temp_logo"
        )
    }

}
