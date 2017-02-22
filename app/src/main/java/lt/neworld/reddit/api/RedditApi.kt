package lt.neworld.reddit.api

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import net.dean.jraw.RedditClient
import net.dean.jraw.http.UserAgent
import net.dean.jraw.http.oauth.Credentials
import net.dean.jraw.models.Submission
import net.dean.jraw.paginators.SubmissionSearchPaginator
import java.util.*

/**
 * @author Andrius Semionovas
 * @since 2017-02-20
 */


object RedditApi {
    val SUBREDIT = "AndroidDev"
    val LIMIT_PER_PAGE = 25

    private val userAgent = UserAgent.of("android", "lt.neworld.reddit", "v0.1", "neworldLT")

    private val redditClient = Single.defer {
        val credentials = Credentials.userlessApp("QyOHfn9fw1y08A", UUID.randomUUID())
        val redditClient = RedditClient(userAgent)
        val oAuthData = redditClient.oAuthHelper.easyAuth(credentials)
        redditClient.authenticate(oAuthData)
        Single.just(redditClient)
    }
            .subscribeOn(Schedulers.io())
            .cache()

    val submissionLoader by lazy {
        SubmissionLoader(redditClient)
    }

    class SubmissionLoader(
            redditClient: Single<RedditClient>
    ) {

        private val loaded = mutableListOf<Submission>()
        private val subject = BehaviorSubject.create<List<Submission>>()

        private val paginator = redditClient.map { redditClient ->
            Log.d(TAG, "initialized paginator")
            SubmissionSearchPaginator(redditClient, "").apply {
                subreddit = SUBREDIT
                setLimit(LIMIT_PER_PAGE)
            }
        }.cache()

        init {
            loadMore()
        }

        var query: String = ""
            set(value) {
                field = value
                notifyChangedItems()
            }

        fun loadMore() {
            if (subject.hasComplete()) {
                Log.e(TAG, "List of submissions are depleted. You shouldn't call loadMore() anymore")
            }
            Log.d(TAG, "request load more")

            paginator.flatMap {
                Single.just(it.next())
            }.subscribeOn(Schedulers.io()).subscribe({ submissions ->
                Log.d(TAG, "loaded ${submissions.size} more items")
                loaded += submissions
                notifyChangedItems()
                if (submissions.size == 0) {
                    Log.d(TAG, "Finished")
                    subject.onComplete()
                }
            }, {
                subject.onError(it)
            })
        }

        private fun notifyChangedItems() {
            Log.d(TAG, "notify about changes")
            subject.onNext(loaded.filter { it.title.contains(query) })
        }

        val onChanged: Observable<List<Submission>> = subject

        companion object {
            private const val TAG = "SubmissionLoader"
        }
    }
}