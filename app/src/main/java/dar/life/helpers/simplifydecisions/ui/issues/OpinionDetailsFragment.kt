package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import dar.life.helpers.simplifydecisions.R

/**
 * A simple [Fragment] subclass.
 */
class OpinionDetailsFragment : Fragment() {

    private lateinit var viewModel: OpinionViewModel
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(OpinionViewModel::class.java)
        //TODO: if new opinion - show editText (also in decisions and issues)
    }

}
