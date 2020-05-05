package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import dar.life.helpers.simplifydecisions.R

class IconsAdapter(context: Context, objects: MutableList<Drawable>) :
    ArrayAdapter<Drawable>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View{
        val view = convertView
            ?: LayoutInflater.from(context)
                .inflate(R.layout.spinner_item, parent, false)

        val imageView: ImageView = view.findViewById(R.id.icons_item_iv)

        var iconDraw = getItem(position)
        iconDraw?.let { imageView.setImageDrawable(iconDraw)}

        return view
    }


}