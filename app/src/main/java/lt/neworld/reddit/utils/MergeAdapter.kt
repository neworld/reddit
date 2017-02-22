package lt.neworld.reddit.utils

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import java.util.ArrayList

/**
 * @author Andrius Semionovas
 */
class MergeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val adapters = ArrayList<RecyclerView.Adapter<*>>()
    private val viewTypeMap = SparseArray<ViewTypeHolder>()

    init {
        setHasStableIds(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewTypeHolder = viewTypeMap.get(viewType)
        return viewTypeHolder.adapter.onCreateViewHolder(parent, viewTypeHolder.viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var position = position
        for (adapter in adapters) {
            val size = adapter.itemCount

            if (position < size) {
                val castedAdapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
                castedAdapter.onBindViewHolder(holder, position)
                return
            }

            position -= size
        }

        throw IndexOutOfBoundsException("Position is out of bounds #" + position)
    }

    override fun getItemCount(): Int {
        return adapters.fold(0) { total, cur -> total + cur.itemCount }
    }

    private fun indexOfViewType(viewTypeHolder: ViewTypeHolder): Int {
        for (i in 0..viewTypeMap.size() - 1) {
            if (viewTypeMap.valueAt(i) == viewTypeHolder) {
                return viewTypeMap.keyAt(i)
            }
        }

        return -1
    }

    override fun getItemViewType(position: Int): Int {
        var position = position
        for (adapter in adapters) {
            val size = adapter.itemCount

            if (position < size) {
                val viewTypeHolder = ViewTypeHolder(adapter, adapter.getItemViewType(position))

                var index = indexOfViewType(viewTypeHolder)
                if (index < 0) {
                    index = viewTypeMap.size()
                    viewTypeMap.put(index, viewTypeHolder)
                }

                return index
            }

            position -= size
        }

        throw IndexOutOfBoundsException("Position is out of bounds #" + position)
    }


    private fun getAdapterStartPosition(adapter: RecyclerView.Adapter<*>): Int {
        var position = 0

        for (iterAdapter in adapters) {
            if (iterAdapter === adapter) {
                return position
            }

            position += iterAdapter.itemCount
        }

        throw IllegalArgumentException("MergeAdapter doesn't contains this adapter")
    }

    fun addAdapter(adapter: RecyclerView.Adapter<*>?) {
        if (adapter == null) {
            return
        }

        val itemCountBefore = itemCount

        adapter.registerAdapterDataObserver(MergeAdapterDataObserver(adapter))
        adapters.add(adapter)

        notifyItemRangeInserted(itemCountBefore, adapter.itemCount)
    }

    /**
     * @return first occurrence of adapter of provided class or null
     */
    fun <T : RecyclerView.Adapter<*>> getFirstAdapter(clazz: Class<T>): T? {
        return adapters.find { clazz.isInstance(it) } as T
    }

    private inner class MergeAdapterDataObserver(val adapter: RecyclerView.Adapter<*>) : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(getAdapterStartPosition(adapter) + positionStart, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(getAdapterStartPosition(adapter) + positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(getAdapterStartPosition(adapter) + positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            val startPosition = getAdapterStartPosition(adapter)

            for (i in 0..itemCount - 1) {
                notifyItemMoved(startPosition + fromPosition + i, startPosition + toPosition + i)
            }
        }
    }

    private data class ViewTypeHolder(val adapter: RecyclerView.Adapter<*>, val viewType: Int)
}
