package ie.fastway.scansort.views.networkinfo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.tubbert.powdroid.ui.BaseUiWidget
import ie.fastway.scansort.R

/**
 *
 */
class NetworkingInfoBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseUiWidget(context, attrs, defStyleAttr) {

    lateinit var progressSpinner: ProgressBar
    lateinit var messageTx: TextView
    lateinit var eventAtTx: TextView

    override fun onPostInflate() {
        progressSpinner = findViewById(R.id.networkingInfo_loadingBar)
        messageTx = findViewById(R.id.networkingInfo_message)
        eventAtTx = findViewById(R.id.networkingInfo_eventAt)
        showNoEvent()
    }

    fun showNoEvent() {
        progressSpinner.visibility = View.INVISIBLE
        messageTx.text = ""
        eventAtTx.text = ""
    }

    override fun getLayoutId(): Int = R.layout.widget_networking_info_bar
}