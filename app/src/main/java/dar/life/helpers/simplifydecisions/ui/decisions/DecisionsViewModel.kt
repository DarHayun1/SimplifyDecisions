package dar.life.helpers.simplifydecisions.ui.decisions

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.DecisionModel
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DecisionsViewModel(application: Application) : AndroidViewModel(application) {

    var gotDecisionsFirstHelp = false
        get() {
            if (!field){
                field = true
                return false
            }
            return true
        }
    private var openedDecision: DecisionModel? = null
    var lastUsedDecision: DecisionModel? = null
    private val repository = AppRepository.getInstance(application.applicationContext)

    fun getAllDecisions(): LiveData<List<DecisionModel>> = repository.allDecisions

    fun getDecisionById(id: Int): LiveData<DecisionModel?>{
        return repository.getDecision(id)
    }
    fun updateDecision(decision: DecisionModel) {
        repository.updateDecision(decision)
    }

    fun addNewDecision(decision: DecisionModel){
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
