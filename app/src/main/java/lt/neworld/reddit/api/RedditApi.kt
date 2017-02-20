package lt.neworld.reddit.api

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.dean.jraw.RedditClient
import net.dean.jraw.http.UserAgent
import net.dean.jraw.http.oauth.Credentials
import net.dean.jraw.models.Subreddit
import java.util.*

/**
 * @author Andrius Semionovas
 * @since 2017-02-20
 */
object RedditApi {
    private val userAgent = UserAgent.of("android", "lt.neworld.reddit", "v0.1", "neworldLT")

    private val redditClient = Single.defer {
        val credentials = Credentials.userlessApp("QyOHfn9fw1y08A", UUID.randomUUID())
        val redditClient = RedditClient(userAgent)
        val oAuthData = redditClient.oAuthHelper.easyAuth(credentials)
        redditClient.authenticate(oAuthData)
        Single.just(redditClient)
    }
            .subscribeOn(Schedulers.computation())
            .cache()

    fun listRecent(): Single<Subreddit> = redditClient.map { it.getSubreddit("AndroidDev") }
}