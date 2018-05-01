package com.tubbert.powdroid.logging

import timber.log.Timber

/**
 * A [Timber.Tree] logger that prints to the standard output, which is useful when
 * debugging unit tests.
 *
 * REMEMBER: If this is planted in a setUp method, then it must be uprooted in the tearDown.
 *
 */
object UnitTestingTree : Timber.Tree() {

    var lastMessage: LogMessage? = null
        private set

    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
        lastMessage = LogMessage(priority, tag, message, t)

        System.out.println("{$tag}[$priority]: $message")
    }

    data class LogMessage(
            val priority: Int,
            val tag: String?,
            val message: String?,
            val throwable: Throwable?
    )
}