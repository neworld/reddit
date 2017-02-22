package lt.neworld.reddit.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * @author Andrius Semionovas
 */
class ViewAdapter(val view: View) : RecyclerView.Adapter<ViewAdapter.ViewHolder>() {

    init {
        setHasStableIds(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewParent = view.parent as ViewGroup?
        viewParent?.removeView(view)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Nothing here
    }

    override fun getItemCount(): Int = 1

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
