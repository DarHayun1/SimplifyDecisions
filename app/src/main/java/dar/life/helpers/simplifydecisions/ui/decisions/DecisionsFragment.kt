package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.databinding.FragmentDecisionsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [DecisionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DecisionsFragment : Fragment() {

    private lateinit var mDecisionsViewModel: DecisionsViewModel
    private lateinit var mContext: Context

    private var _binding: FragmentDecisionsBinding? = null
    private val binding get() = _binding!!

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DecisionsFragment.
         */
        @JvmStatic
        fun newInstance() =
            DecisionsFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDecisionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDecisionsViewModel = ViewModelProvider(this).get(DecisionsViewModel::class.java)

        initViews()


    }

    private fun initViews() {

        val rvAdapter = DecisionsAdapter(mContext)
        binding.decisionsRv.adapter = rvAdapter
        binding.decisionsRv.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.decisionsRv.addItemDecoration(
            DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL)
        )

        mDecisionsViewModel.getAllDecisions().observe(viewLifecycleOwner, Observer {
            rvAdapter.setData(it)
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
