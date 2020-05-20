package dar.life.helpers.simplifydecisions.ui.issues

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dar.life.helpers.simplifydecisions.R

class IssuePagerAdapter : PagerAdapter() {

    override fun getCount(): Int = 2

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = container.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var resId = -1

        when (position) {
            0 ->
                resId = R.layout.compare_options_layout
            1 ->
                resId = R.layout.unfinished_tasks_layout
        }

        val view = inflater.inflate(resId, container, false)
        (container as ViewPager).addView(view, 0)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
