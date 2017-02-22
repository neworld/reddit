package lt.neworld.reddit.utils

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner

/**
 * @author Andrius Semionovas
 */
@RunWith(RobolectricTestRunner::class)
class MergeAdapterTest {

    private val viewGroup: ViewGroup = mock()
    private val adapterA: AdapterA = spy(AdapterA())
    private val adapterB: AdapterB = spy(AdapterB())
    private val adapter: MergeAdapter = MergeAdapter().apply {
        addAdapter(adapterA)
        addAdapter(adapterB)
    }

    @Test
    fun testOnCreateViewHolderOnA() {
        val viewType = adapter.getItemViewType(1)

        adapter.onCreateViewHolder(viewGroup, viewType)

        verify<AdapterA>(adapterA).onCreateViewHolder(eq(viewGroup), anyInt())
    }

    @Test
    fun testOnCreateViewHolderOnB() {
        val viewType = adapter.getItemViewType(3)

        adapter.onCreateViewHolder(viewGroup, viewType)

        verify<AdapterB>(adapterB).onCreateViewHolder(eq(viewGroup), anyInt())
    }

    @Test
    fun testOnBindViewHolderA() {
        val viewHolder: ViewAdapter.ViewHolder = mock()

        adapter.onBindViewHolder(viewHolder, 0)

        verify<AdapterA>(adapterA).onBindViewHolder(eq(viewHolder), eq(0))
    }

    @Test
    fun testOnBindViewHolderB() {
        val viewHolder: ViewAdapter.ViewHolder= mock()

        adapter.onBindViewHolder(viewHolder, 3)

        verify<AdapterB>(adapterB).onBindViewHolder(eq(viewHolder), eq(0))
    }

    @Test
    fun testDataChangedObserverA() {
        val observer: RecyclerView.AdapterDataObserver = mock()

        adapter.registerAdapterDataObserver(observer)

        adapterA.notifyDataSetChanged()
        adapterA.notifyItemRangeInserted(0, 1)
        adapterA.notifyItemRangeChanged(0, 1)
        adapterA.notifyItemRangeRemoved(0, 1)

        verify(observer).onChanged()
        verify(observer).onItemRangeChanged(0, 1, null)
        verify(observer).onItemRangeInserted(0, 1)
        verify(observer).onItemRangeRemoved(0, 1)
    }

    @Test
    fun testDataChangedObserverB() {
        val observer: RecyclerView.AdapterDataObserver = mock()

        adapter.registerAdapterDataObserver(observer)

        adapterB.notifyDataSetChanged()
        adapterB.notifyItemRangeInserted(0, 1)
        adapterB.notifyItemRangeChanged(0, 1)
        adapterB.notifyItemRangeRemoved(0, 1)

        val startPosition = adapterA.itemCount

        verify(observer).onChanged()
        verify(observer).onItemRangeChanged(startPosition, 1, null)
        verify(observer).onItemRangeInserted(startPosition, 1)
        verify(observer).onItemRangeRemoved(startPosition, 1)
    }

    @Test
    fun testGetItemCount() {
        val totalItemCount = adapterA.itemCount + adapterB.itemCount
        assertEquals(totalItemCount.toLong(), adapter.itemCount.toLong())
    }

    @Test
    fun testGetItemViewType() {
        val a = adapter.getItemViewType(0)
        val b = adapter.getItemViewType(4)
        val c = adapter.getItemViewType(0)

        assertEquals(a.toLong(), c.toLong())
        assertNotEquals(a.toLong(), b.toLong())
    }

    @Test
    fun testAddNullAdapter() {
        adapter.addAdapter(null)
    }
}

open class AdapterA(val itemsCount: Int = 3) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return mock<ViewAdapter.ViewHolder>()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount() = itemsCount

    override fun getItemViewType(position: Int) = position
}

open class AdapterB : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return mock<ViewAdapter.ViewHolder>()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount() = 2

    override fun getItemViewType(position: Int)= -1 + position
}
