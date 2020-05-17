package dar.life.helpers.simplifydecisions.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.R


class UiUtils {


    companion object {
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
        // Returns true if the edit is done
        fun handleEditTitleClick(
            context: Context,
            textView: TextView,
            editTextLayout: TextInputLayout,
            editText: TextInputEditText,
            icon: ImageView
        ): Boolean {
            if (textView.visibility == View.VISIBLE) {
                textView.visibility = View.INVISIBLE
                editTextLayout.visibility = View.VISIBLE
                editText.setText(textView.text)
                icon.setImageDrawable(
                    context
                        .getDrawable(R.drawable.confirm_edit_icon)
                )
                return false
            }
            textView.visibility = View.VISIBLE
            editTextLayout.visibility = View.INVISIBLE
            icon.setImageDrawable(
                (context
                    .getDrawable(R.drawable.pencil_edit_icon))
            )
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(
                editText.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
            return true

        }

        fun setColorFilter(drawable: Drawable, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

}