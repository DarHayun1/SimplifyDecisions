package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.ui.UiUtils

class ColorsAdapter(context: Context, colorsList: List<Int>) :
    ArrayAdapter<Int>(context, 0, colorsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context)
                .inflate(R.layout.color_spinner_item, parent, false)


        val colorView = view.findViewById<View>(R.id.color_item_frame)
//        colorView.background = ContextCompat.getDrawable(context, R.drawable.round_icon_background)
        val color = getItem(position)
        color?.let { UiUtils.setColorFilter(colorView.background, it)}

        return view
    }

}
