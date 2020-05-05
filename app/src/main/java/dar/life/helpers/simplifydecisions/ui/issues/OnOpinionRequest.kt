package dar.life.helpers.simplifydecisions.ui.issues

import android.view.View
import dar.life.helpers.simplifydecisions.data.Opinion

interface OnOpinionRequest {

    fun openOpinionScreen(opinion: Opinion, titleView: View)

}
