package dar.life.helpers.simplifydecisions.ui.decisions

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.ReminderObj
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DecisionsViewModel(application: Application) : AndroidViewModel(application) {

    var lastUsedDecision: Decision? = null
    private val repository = AppRepository.getInstance(application.applicationContext)

    fun getAllDecisions(): LiveData<List<Decision>> = repository.allDecisions
    fun getDecisionById(id: Int): LiveData<Decision>{
        return repository.getDecision(id)
    }
    fun updateDecision(decision: Decision) {
        repository.updateDecision(decision)
    }

    fun addNewDecision(decision: Decision){
        lastUsedDecision = decision
        repository.addNewDecision(decision)
    }

}
