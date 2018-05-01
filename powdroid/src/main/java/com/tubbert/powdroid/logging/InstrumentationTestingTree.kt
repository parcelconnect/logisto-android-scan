package com.tubbert.powdroid.logging

import timber.log.Timber

/**
 * Logging [Timber.Tree] compatible with Instrumentation tests.
 */
object InstrumentationTestingTree :Timber.Tree() {

    private val debugTree = Timber.DebugTree()

    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
        debugTree.log(priority, tag, message, t)
    }
}