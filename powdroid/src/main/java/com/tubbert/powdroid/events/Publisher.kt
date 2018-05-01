package com.tubbert.powdroid.events

import com.tubbert.powdroid.logging.NonLoggingTree
import timber.log.Timber

/**
 * Implements a simple publish-subscribe pattern.
 * Note, this is implementation is designed to catch exceptions that occur in listeners and logger them, but
 * NOT to re-throw exceptions by default. This is obviously dangerous behaviour, so
 * override the [handleListenerException] method if this behaviour is not desired.
 *
 */
open class Publisher<SubscriberT>
    : Iterable<SubscriberT>, Subscribable<SubscriberT> {

    protected val registeredSubsribers: MutableSet<SubscriberT> = HashSet()

    var logger: Timber.Tree = NonLoggingTree()

    var shouldPrintListenersToLogs: Boolean = false

    protected val registeredSubscribers: Collection<SubscriberT>
        get() = this.registeredSubsribers

    val logTag: String
        get() = javaClass.simpleName

    override fun subscribe(subscriber: SubscriberT?) {
        if (subscriber != null && !registeredSubsribers.contains(subscriber)) {
            registeredSubsribers.add(subscriber)
        }
        else {
            logger.e(logTag + "; Cannot register subscriber: " + subscriber)
        }
    }

    override fun unsubscribe(subscriberToRelease: SubscriberT) {
        registeredSubsribers.remove(subscriberToRelease)
    }

    fun executeOnListeners(taskToExecute: Executor<SubscriberT>) {
        for (listener in this) {
            try {
                if (shouldPrintListenersToLogs) {
                    logger.v(logTag + "; Alerting listener: " + listener)
                }
                taskToExecute.execute(listener)
            }
            catch (e: Exception) {
                handleListenerException(listener, e)
            }

        }
    }

    protected fun handleListenerException(listener: SubscriberT, error: Throwable) {
        Timber.e(error, logTag + "; Executor Exception! Listener=" + listener)
    }

    override fun iterator(): Iterator<SubscriberT> {
        return registeredSubsribers.iterator()
    }

    /**
     * Moves through the chain of registered Subscribers and executes the [eventTask]
     * on it, until one of the subscribers returns TRUE, indicating that the task
     * has been handled by that subscriber and the others need not be notified of it.
     *
     * @param eventTask The task to run against each registered [SubscriberT]. Should
     *  return TRUE when a [SubscriberT] handles the [eventTask] in such a way as
     *  to indicate that the other [SubscriberT]s need not be notified of the event.
     *
     * @return TRUE if one of the subscribers handled the [eventTask],
     * FALSE if the task was not handled.
     *
     * Return TRUE to PREVENT this event from being propagated
     * further, or FALSE to indicate that you have not handled
     * this event and it should continue to be propagated.
     */
    protected inline fun publishUntilHandled(
            crossinline eventTask: (SubscriberT) -> Boolean): Boolean {
        var isHandled = false
        executeOnListeners(Executor {
            if (eventTask(it)) {
                isHandled = true
                return@Executor
            }
        })
        return isHandled
    }

}
