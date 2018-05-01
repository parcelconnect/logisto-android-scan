package com.tubbert.powdroid.logging

import timber.log.Timber

/**
 * A logging tree that does not write any logs.
 */
class NonLoggingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {}

}