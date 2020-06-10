package dar.life.helpers.simplifydecisions.ui.decisions

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.ReminderObj
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DecisionsViewModel(application: Application) : AndroidViewModel(application) {

    private var openedDecision: Decision? = null
    var lastUsedDecision: Decision? = null
    private val repository = AppRepository.getInstance(application.applicationContext)

    fun getAllDecisions(): LiveData<List<Decision>> = repository.allDecisions

    fun getDecisionById(id: Int): LiveData<Decision>{
        return repository.getDecision(id)
    }
    fun updateDecision(decision: Decision) {
        Log.d("DeleteBug1", "decisiongoals: ${decision.goals}")
        repository.updateDecision(decision)
    }

    fun addNewDecision(decision: Decision){
        lastUsedDecision = decision
        repository.addNewDecision(decision)
    }

    fun isFirstInit(): Boolean {
        if (openedDecision != lastUsedDecision) {
            openedDecision = lastUsedDecision
            return true
        }
        return false

    }

}
