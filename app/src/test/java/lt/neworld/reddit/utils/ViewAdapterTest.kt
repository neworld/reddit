package lt.neworld.reddit.utils

import android.view.View
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * @author Andrius Semionovas
 */
@RunWith(RobolectricTestRunner::class)
class ViewAdapterTest {

    val view: View = mock()
    val adapter = ViewAdapter(view)

    @Test
    fun testOnCreateViewHolder() {
        val viewHolder = adapter.createViewHolder(mock(), 0)

        assertSame(view, viewHolder.itemView)
    }

    @Test
    fun testGetItemCount() {
        assertEquals(1, adapter.itemCount.toLong())
    }
}