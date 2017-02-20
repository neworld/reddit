package lt.neworld.reddit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import lt.neworld.reddit.R
import lt.neworld.reddit.api.RedditApi

class RecentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            loadRecent()
        }
    }

    private fun loadRecent() {
        RedditApi.listRecent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("TEST", it.toString())
                }, {
                    Log.e("TEST", it.toString())
                })
    }

    companion object {
        fun newInstance() = RecentFragment()
    }
}