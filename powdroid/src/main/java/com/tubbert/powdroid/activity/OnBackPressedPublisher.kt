package com.tubbert.powdroid.activity

import com.tubbert.powdroid.events.Publisher

/**
 * Handles forwarding back-button press events to listeners.
 */
class OnBackPressedPublisher : Publisher<OnBackPressedListener>(), OnBackPressedListener {

    override fun onBackButtonPressed(): Boolean =
            publishUntilHandled { it.onBackButtonPressed() }

}