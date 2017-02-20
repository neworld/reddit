package lt.neworld.reddit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_preview.*
import net.dean.jraw.models.Submission

class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val submissionUrl = intent.getStringExtra(ARG_SUBMISSION_URL)
        val submissionTitle = intent.getStringExtra(ARG_SUBMISSION_TITLE)

        title = submissionTitle
        activity_preview_webview.loadUrl(submissionUrl)
    }

    companion object {
        private const val ARG_SUBMISSION_URL = "submission_url"
        private const val ARG_SUBMISSION_TITLE = "submission_title"

        fun createIntent(context: Context, submission: Submission): Intent {
            return Intent(context, PreviewActivity::class.java).apply {
                putExtra(ARG_SUBMISSION_URL, submission.url)
                putExtra(ARG_SUBMISSION_TITLE, submission.title)
            }
        }
    }
}
