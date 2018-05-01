package com.tubbert.powdroid.activity;

/**
 * Listens for when the hard back-button is pressed.
 */
public interface OnBackPressedListener {

    /**
     *
     * @return TRUE if this listener has handled the back operation, and therefore other
     * listeners do not need to be notified of this back-pressed event.
     */
    public boolean onBackButtonPressed();

}
