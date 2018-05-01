package com.tubbert.powdroid.activity

import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import timber.log.Timber

/**
 *
 */
open class BaseMajorActivity : AppCompatActivity() {

    protected val backButtonPublisher = OnBackPressedPublisher()

    private val keyEventPublisher = KeyEventPublisher()

    //----------------------------------------------------------------------------------------------
    // KeyEvent.Callback
    //----------------------------------------------------------------------------------------------

    open fun registerKeyEventCallback(callback: KeyEvent.Callback) {
        keyEventPublisher.subscribe(callback)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        logEvent(keyCode, event, "Up")

        keyEventPublisher.onKeyUp(keyCode, event)

        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        logEvent(keyCode, event, "Down")

        keyEventPublisher.onKeyDown(keyCode, event)

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        logEvent(keyCode, event, "LongPress")

        keyEventPublisher.onKeyLongPress(keyCode, event)

        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent): Boolean {
        logEvent(keyCode, event, "Multiple")

        keyEventPublisher.onKeyMultiple(keyCode, repeatCount, event)

        return super.onKeyMultiple(keyCode, repeatCount, event)
    }

    override fun onKeyShortcut(keyCode: Int, event: KeyEvent): Boolean {
        logEvent(keyCode, event, "Shortcut")

        return super.onKeyShortcut(keyCode, event)
    }

    private inline fun logEvent(keyCode: Int, event: KeyEvent, methodName: String) {
        // This is inlined to ensure that the compiler will remove it entirely when the
        // function body is commented out.

        Timber.v("onKeyEvent; keyCode=$keyCode; event=$event; method=onKey$methodName")
    }

}