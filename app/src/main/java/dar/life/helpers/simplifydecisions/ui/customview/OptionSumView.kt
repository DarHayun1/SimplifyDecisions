package dar.life.helpers.simplifydecisions.ui.customview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.ui.UiUtils


class OptionSumView : LinearLayout {
    private var containerView: View? = null
    private var opinion1Tv: TextView? = null
    private var opinion2Tv: TextView? = null
    private var opinion3Tv: TextView? = null
    private var pointsTv: TextView? = null
    private var titleTv: TextView? = null
    private var iconIv: ImageView? = null

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    /*
        Initialize views
     */
    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        View.inflate(getContext(), R.layout.option_sum_layout,
            this)
        containerView = findViewById(R.id.pick_option_container)
        iconIv = findViewById(R.id.pick_option_iv)
        titleTv = findViewById(R.id.pick_option_tv)
        pointsTv = findViewById(R.id.option_points_tv)
        opinion1Tv = findViewById(R.id.option_opinion_1)
        opinion2Tv = findViewById(R.id.option_opinion_2)
        opinion3Tv = findViewById(R.id.option_opinion_3)
    }

    fun setData(drawable: Drawable?, title: String, score: Int, opinion1: Opinion?, opinion2: Opinion?, opinion3: Opinion?){
        drawable?.let { iconIv!!.setImageDrawable(it) }
        titleTv?.text = title
        pointsTv?.let {
            it.text = score.toString()
        }
        opinion1Tv?.text = opinion1?.title
        opinion2Tv?.text = opinion2?.title
        opinion3Tv?.text = opinion3?.title
        setupView()
    }

    fun setBackgroudColor(color: Int) {
        containerView?.background?.let {
            val transColor =
                Color.argb(110, Color.red(color), Color.green(color), Color.blue(color))
            UiUtils.setColorFilter(it.mutate(), transColor)
        }
    }

    private fun setupView() {
    }

}