package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.ui.UiUtils

class TopicsAdapter(val mContext: Context, val mCallBack: OnTemplateClickedListener):
    RecyclerView.Adapter<TopicsAdapter.TopicsVH>() {

    val topics: List<String> = mContext.resources.getStringArray(R.array.topics).toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicsVH {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.template_item, parent, false)
        return TopicsVH(view)
    }

    override fun getItemCount(): Int = topics.size

    override fun onBindViewHolder(holder: TopicsVH, position: Int) {
        val drawId = mContext.resources.getIdentifier("${topics[position]}_icon" ,
            "drawable", mContext.packageName)
        try {
            mContext.getDrawable(drawId)?.let {
                holder.bindItem(it)
                if (position == itemCount - 1)
                    UiUtils
                        .setColorFilter(
                            it,
                            ContextCompat.getColor(mContext, R.color.app_green_light)
                        )
            }
        }catch (e: android.content.res.Resources.NotFoundException){}
        holder.itemView.setOnClickListener{
            mCallBack.onTemplateClicked(topics[position])
        }
    }

    class TopicsVH(itemView: View) :
        RecyclerView.ViewHolder(itemView){

        val iconIv: ImageView = itemView.findViewById(R.id.template_icon_iv)

        fun bindItem(iconDrawable: Drawable){
            iconIv.setImageDrawable(iconDrawable)
        }
    }
}