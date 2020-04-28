package dar.life.helpers.simplifydecisions.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat

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

        fun buttonEffect(vararg buttons: View) {
            buttons.forEach {
                it.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.background.colorFilter = BlendModeColorFilterCompat
                                .createBlendModeColorFilterCompat(-0x1f0b8adf,
                                    BlendModeCompat.SRC_ATOP)
                            v.invalidate()
                        }
                        MotionEvent.ACTION_UP -> {
                            v.background.colorFilter = null
                            v.invalidate()
                        }
                    }
                    false
                }
            }

        }
    }

}