package com.tubbert.powdroid.activity

import android.view.KeyEvent
import com.tubbert.powdroid.events.Publisher

/**
 * Publishes [KeyEvent]s to registered listeners.
 */
class KeyEventPublisher : Publisher<KeyEvent.Callback>(), KeyEvent.Callback{

    override fun onKeyMultiple(keyCode: Int, count: Int, event: KeyEvent?): Boolean {
        return publishUntilHandled { it.onKeyMultiple(keyCode, count, event) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return  publishUntilHandled { it.onKeyDown(keyCode, event) }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return publishUntilHandled { it.onKeyUp(keyCode, event) }
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return publishUntilHandled { it.onKeyLongPress(keyCode, event) }
    }
}