package dar.life.helpers.simplifydecisions.ui.issues

import android.view.View

interface OnIssueEditClick {
    fun openIssueDetails(issueId: String, issueTitle: String, view: View)
}