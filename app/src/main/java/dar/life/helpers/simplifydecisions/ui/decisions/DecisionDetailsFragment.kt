package dar.life.helpers.simplifydecisions.ui.decisions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import dar.life.helpers.simplifydecisions.R

class DecisionDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DecisionDetailsFragment()
    }

    private lateinit var viewModel: DecisionDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.decision_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DecisionDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
