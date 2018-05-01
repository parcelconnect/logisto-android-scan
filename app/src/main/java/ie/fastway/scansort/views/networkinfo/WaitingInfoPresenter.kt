package ie.fastway.scansort.views.networkinfo

import android.view.View
import com.squareup.otto.Subscribe
import ie.fastway.scansort.logging.LogConfig
import ie.logistio.equinox.Equinox
import timber.log.Timber

/**
 * Presents networking info to the user so that they can understand what
 * long-running tasks are being performed by the app.
 */
class WaitingInfoPresenter(
        private val networkingView: NetworkingInfoBar) {

    @Subscribe
    fun onNetworkingEvent(event: UserWaitingEventUpdate) {

        if (LogConfig.NETWORKING_EVENT) {
            Timber.d("onNetworkingEvent:$event")
        }

        if (event.isPending) {
            showPendingEvent(event)
        }
        else {
            showFinishedEvent(event)
        }

    }

    private fun showPendingEvent(event: UserWaitingEventUpdate) {

        val messageData: Pair<String, Boolean> = when {
            event.progressValue == null ->
                Pair(event.userMessage, true)

            event.progressMax == null ->
                Pair("${event.userMessage} (${event.progressValue}%)",
                        event.progressValue < 100)

            else ->
                Pair("${event.userMessage} (${event.progressValue}/${event.progressMax})",
                        event.progressValue < event.progressMax)
        }

        networkingView.messageTx.text = messageData.first
        networkingView.progressSpinner.visibility = if (messageData.second) View.VISIBLE else View.INVISIBLE

        networkingView.eventAtTx.text = Equinox.convertToLocalTimeOfDay(event.eventAt)
    }

    private fun showFinishedEvent(event: UserWaitingEventUpdate) {
        with(networkingView) {
            progressSpinner.visibility = View.INVISIBLE
            messageTx.text = ""
            eventAtTx.text = ""
        }
    }
}