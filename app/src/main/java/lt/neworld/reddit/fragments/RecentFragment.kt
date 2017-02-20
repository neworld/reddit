package lt.neworld.reddit.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recent.*
import kotlinx.android.synthetic.main.list_item.view.*
import lt.neworld.reddit.PreviewActivity
import lt.neworld.reddit.R
import lt.neworld.reddit.api.RedditApi
import lt.neworld.reddit.utils.SimpleViewHolder
import net.dean.jraw.models.Submission

class RecentFragment : Fragment() {

    private val adapter = Adapter()
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            loadRecent()
        }

        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val manager = recyclerView.layoutManager as LinearLayoutManager
                val firstPos = manager.findFirstVisibleItemPosition()
                val lastPos = manager.findLastVisibleItemPosition()

                if (firstPos == RecyclerView.NO_POSITION || lastPos == RecyclerView.NO_POSITION) {
                    return
                }

                val fit = lastPos - firstPos
                if (recyclerView.adapter.itemCount - fit < lastPos) {
                    RedditApi.submissionLoader.loadMore()
                }
            }
        })
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

    private fun loadRecent() {
        val progressDialog = ProgressDialog.show(activity, "", "Loading", true)

        disposable = RedditApi.submissionLoader.onChanged
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { progressDialog.hide() }
                .subscribe({ submissions ->
                    adapter.submissions = submissions
                }, {
                    Log.e(TAG, "Failed get changed items", it)
                })
    }

    class Adapter : RecyclerView.Adapter<SimpleViewHolder>() {
        var submissions: List<Submission> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.list_item, parent, false)
            return SimpleViewHolder(view)
        }

        override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
            val submission = submissions[position]
            val context = holder.itemView.context

            holder.itemView.item_list_text.text = submission.title

            Picasso.with(context)
                    .load(submission.thumbnail)
                    .error(R.drawable.ic_error_photo_24dp)
                    .into(holder.itemView.item_list_image)

            holder.itemView.setOnClickListener {
                context.startActivity(PreviewActivity.createIntent(context, submission))
            }
        }

        override fun getItemCount() = submissions.size
    }

    companion object {
        private const val TAG = "RecentFragment"

        fun newInstance() = RecentFragment()
    }
}