package dar.life.helpers.simplifydecisions.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.Constants.BLUE_LIGHT
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_A_COLOR
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_A_ICON
import dar.life.helpers.simplifydecisions.Constants.GREY
import dar.life.helpers.simplifydecisions.Constants.LIGHT_GREEN
import dar.life.helpers.simplifydecisions.Constants.PURPLE
import dar.life.helpers.simplifydecisions.Constants.RED_LIGHT
import dar.life.helpers.simplifydecisions.Constants.TURQUOISE
import dar.life.helpers.simplifydecisions.Constants.YELLOW_DARK
import dar.life.helpers.simplifydecisions.R


class UiUtils {


    companion object {
        private val shortAnimationDuration = 200L

        fun fadeInViews(vararg views: View) {
            views
                .forEach { view: View ->
                    if (view.visibility != View.VISIBLE) {
                        view.alpha = 0f
                        view.visibility = View.VISIBLE
                        view.animate().alpha(1f).setDuration(shortAnimationDuration)
                            .setListener(null)
                    }
                }
        }

        fun fadeOutViews(vararg views: View) {
            views.forEach { view: View ->
                if (view.visibility == View.VISIBLE) {
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

        fun colorNames() = listOf<String>(
            PURPLE,
            LIGHT_GREEN,
            YELLOW_DARK,
            GREY,
            TURQUOISE,
            BLUE_LIGHT,
            RED_LIGHT
        )

        fun nameToColor(colorName: String, context: Context): Int{
            val id = context.resources.getIdentifier(
                colorResFormat(colorName),
                "color", context.packageName)
            if (id == 0) return Color.parseColor(DEFAULT_A_COLOR)
            return ContextCompat.getColor(context, id)
        }

        fun nameToIcon(colorName: String, context: Context): Drawable {
            var id = context.resources.getIdentifier(
                iconResFormat(colorName),
                "drawable", context.packageName
            )
            if (id == 0)
                id = context.resources.getIdentifier(
                    iconResFormat(DEFAULT_A_ICON),
                    "drawable", context.packageName
                )
            return ContextCompat.getDrawable(context, id)!!
        }

        fun iconResFormat(colorName: String) =
            "${colorName}_avatar"

        fun colorResFormat(colorName: String) =
            "app_${colorName}_trans"

        fun getColors(context: Context): List<Int> {
            return colorNames().map {
                nameToColor(it, context)
            }
        }

        fun getIconsList(context: Context): MutableList<Drawable> {
            return colorNames().map {
                return@map nameToIcon(it, context)
            }.toMutableList()
        }



        fun setImportanceColor(view: TextView, importance: Int, context: Context) {
            val background = view.background
            view.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (importance < 50) R.color.primary_text_light else R.color.primary_text_dark
                ))
            background.alpha = 220
            val colorArray = context.resources.getIntArray(R.array.progressGradientColors)
            if (importance in 0..100) {
                val startColor = colorArray[(importance-1) / 25]
                val endColor = colorArray[(importance - 1) / 25]
                val resultColor = ArgbEvaluator().evaluate(
                    ((importance - 1).toFloat() % 25 / 25),
                    startColor,
                    endColor
                ) as Int
                ColorUtils.setAlphaComponent(resultColor, 10)
                setColorFilter(
                    background,
                    resultColor)
            }
        }
    }

}