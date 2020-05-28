package dar.life.helpers.simplifydecisions.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.repository.AppRepository

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application.applicationContext)

    fun getAllDecisions(): LiveData<List<Decision>> = repository.allDecisions

}
