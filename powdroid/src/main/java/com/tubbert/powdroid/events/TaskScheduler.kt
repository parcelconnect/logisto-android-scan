package com.tubbert.powdroid.events

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 *
 */
object TaskScheduler {

    fun createDelayedObservable(
            delayMs: Long,
            subscribeScheduler: Scheduler = AndroidSchedulers.mainThread(),
            observeScheduler: Scheduler = AndroidSchedulers.mainThread())
            : Observable<Unit> {

        return Observable.just(Unit).delay(delayMs, TimeUnit.MILLISECONDS)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)

    }

    fun createScheduledTask(
            waitPeriodMs: Long,
            initialDelayMs: Long = waitPeriodMs,
            subscribeScheduler: Scheduler = AndroidSchedulers.mainThread(),
            task: () -> Unit
    ): Disposable {

        return subscribeScheduler.createWorker()
                .schedulePeriodically(task, initialDelayMs, waitPeriodMs, TimeUnit.MILLISECONDS)

    }

}