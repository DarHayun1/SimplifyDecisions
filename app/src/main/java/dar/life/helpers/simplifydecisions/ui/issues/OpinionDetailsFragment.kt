package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.databinding.FragmentOpinionDetailsBinding
import kotlinx.android.synthetic.main.fragment_opinion_details.*


/**
 * A simple [Fragment] subclass.
 */
class OpinionDetailsFragment : Fragment() {

    private var mOpinion: Opinion? = null
    private var mIssue: Issue? = null
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
        if(args.opinionId == -1)
            startWithTitleEdit()
        else{
            binding.opinionTitleTv.text = args.opinionTitle
        }

    }

    private fun startWithTitleEdit() {
        binding.opinionTitleTv.visibility = View.INVISIBLE
        binding.opinionTitleEt.visibility = View.VISIBLE
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
                mIssue = it
                mOpinion = it.opinions[args.opinionId]
                binding.relatedIssueTitle.text = mIssue?.title
                radioGroup.check(
                    when(mOpinion!!.importance){
                        Opinion.LOW_IMPORTANCE -> binding.radButtonLow.id
                        Opinion.MEDIUM_IMPORTANCE -> binding.radButtonMedium.id
                        Opinion.HIGH_IMPORTANCE -> binding.radButtonHigh.id
                        //TODO: Game Changer
                        else -> binding.radButtonHigh.id
                    }
                )

            }
        })
    }

}
