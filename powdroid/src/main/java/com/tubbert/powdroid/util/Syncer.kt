package com.tubbert.powdroid.util

/**
 * Utilities for synchonrising operations.
 */
class Syncer {

    companion object {

        /**
         * Implements a double-checked lock.
         */
        inline fun doubleCheckedLock(
                lock: Any,
                condition: () -> Boolean,
                syncedTask: () -> Unit
        ) {
            if(condition()) {
                synchronized(lock) {
                    if(condition()) syncedTask()
                }
            }
        }

    }

}