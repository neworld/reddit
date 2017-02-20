package lt.neworld.reddit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import lt.neworld.reddit.fragments.RecentFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val pagerAdapter = MyPagerAdapter(supportFragmentManager)
        container.adapter = pagerAdapter
    }


    class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> RecentFragment.newInstance()
                else -> throw RuntimeException("This adapter more than $TOTAL_FRAGMENT")
            }
        }

        override fun getCount() = TOTAL_FRAGMENT

        companion object {
            private const val TOTAL_FRAGMENT = 1
        }
    }
}
