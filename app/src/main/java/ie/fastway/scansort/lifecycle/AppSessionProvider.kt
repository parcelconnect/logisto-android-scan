package ie.fastway.scansort.lifecycle

import com.tubbert.powdroid.events.TaskScheduler
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * Provides access to singletons used across the app.
 */
object AppSessionProvider {

    val eventPublisher = EventPublisher()

    fun runOnBackgroundThread(task: () -> Unit) {
        Schedulers.io().scheduleDirect(task)
    }

    fun createSingleTaskScheduler(
            delayMs: Long,
            subscribeScheduler: Scheduler = AndroidSchedulers.mainThread(),
            observeScheduler: Scheduler = AndroidSchedulers.mainThread()
    ): Observable<Unit> {

        return Observable.just(Unit).delay(delayMs, TimeUnit.MILLISECONDS)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)

    }

    fun createScheduledTask(
            waitPeriodMs: Long,
            initialDelayMs: Long = waitPeriodMs,
            subscribeScheduler: Scheduler = AndroidSchedulers.mainThread(),
            task: () -> Unit
    ) = TaskScheduler.createScheduledTask(waitPeriodMs, initialDelayMs, subscribeScheduler, task)

}