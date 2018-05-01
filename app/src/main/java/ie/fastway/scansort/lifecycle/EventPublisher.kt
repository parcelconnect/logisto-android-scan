package ie.fastway.scansort.lifecycle

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import ie.fastway.scansort.logging.LogConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 *
 */
class EventPublisher {

    private val eventBus = Bus(ThreadEnforcer.ANY, "ie.fastway.scansort.lifecycle.EventPublisher.BUS_MAIN")

    fun postOnUiThread(event: Any) {
        if (LogConfig.EVENT_BUS) {
            Timber.d("postOnUiThread; event=$event")
        }

        AndroidSchedulers.mainThread().scheduleDirect { eventBus.post(event) }
    }

    fun postOnBackgroundThread(event: Any) {
        Schedulers.io().scheduleDirect { eventBus.post(event) }
    }

    fun register(eventListener: Any) {
        eventBus.register(eventListener)
    }

}