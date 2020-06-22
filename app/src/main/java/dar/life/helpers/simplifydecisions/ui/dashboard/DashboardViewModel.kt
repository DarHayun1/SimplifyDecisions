package dar.life.helpers.simplifydecisions.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.DecisionModel
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application.applicationContext)

    fun getAllDecisions(): LiveData<List<DecisionModel>> = repository.allDecisions

}
