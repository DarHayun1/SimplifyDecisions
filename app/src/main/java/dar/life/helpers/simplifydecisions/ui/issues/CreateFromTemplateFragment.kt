package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.databinding.FragmentCreateFromTemplateBinding

class CreateFromTemplateFragment : Fragment(), OnTemplateClickedListener {

    private var _binding: FragmentCreateFromTemplateBinding? = null
    private val binding get() = _binding!!

    private lateinit var mContext: Context

    companion object{
        const val ISSUE_TITLE_KEY = "issue_title"
    }

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
        _binding = FragmentCreateFromTemplateBinding.inflate(layoutInflater, container,
            false)
        initRV()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun initRV() {
        val adapter =
            TopicsAdapter(mContext, this)
        binding.topicsRv.adapter = adapter
        binding.topicsRv.layoutManager =
            GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false)
    }

    override fun onTemplateClicked(template: String) {
        val action =
            CreateFromTemplateFragmentDirections.actionCreateFromTemplateFragmentToNewIssueTitleFragment()
        action.template = template
        val extras = FragmentNavigatorExtras(
            binding.fromTemplateBottomDrawer to getString(R.string.bot_drawer_trans_name),
            binding.smallDrawerLogo to getString(R.string.new_issue_logo_transition)
        )
        findNavController().navigate(action, extras)
    }


}
