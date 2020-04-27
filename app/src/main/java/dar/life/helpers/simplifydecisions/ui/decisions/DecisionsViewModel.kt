package dar.life.helpers.simplifydecisions.ui.decisions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DecisionsViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository = AppRepository.getInstance(application)

    fun getAllDecisions(): LiveData<List<Decision>> = mRepository.allDecisions

}