package dar.life.helpers.simplifydecisions.ui.decisions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DecisionDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)

    fun getDecisionById(id: Int): LiveData<Decision> = repository.getDecision(id)
    fun updateDecision(decision: Decision) {
        repository.updateDecision(decision)
    }

}
