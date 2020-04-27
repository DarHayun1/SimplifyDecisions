package dar.life.helpers.simplifydecisions.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

class UiUtils {


    companion object{
        private val shortAnimationDuration = 200L

        fun fadeInViews(vararg views: View) {
            views
                .forEach { view: View ->
                    view.alpha = 0f
                    view.visibility = View.VISIBLE
                    view.animate().alpha(1f).setDuration(shortAnimationDuration)
                        .setListener(null)
                }
        }

        fun fadeOutViews(vararg views: View) {
            views.forEach { view: View ->
                view.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            view.visibility = View.GONE
                        }
                    })
            }
        }
    }

}