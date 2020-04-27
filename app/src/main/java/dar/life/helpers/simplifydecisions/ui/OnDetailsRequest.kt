package dar.life.helpers.simplifydecisions.ui

import android.view.View

interface OnDetailsRequest {
    fun openDetailsScreen(id: Int, title: String, view: View)
}