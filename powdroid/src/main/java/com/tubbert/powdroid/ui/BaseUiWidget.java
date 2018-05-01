package com.tubbert.powdroid.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Base View for creating widgets based on a {@link LinearLayout}.
 * <p/>
 * <p>
 * Child classes must extend all of the available constructors, but do not need to
 * initialise views or logic within them, as {@link #onPostInflate()} will be called.
 * </p>
 */
public abstract class BaseUiWidget extends LinearLayout {

    public BaseUiWidget(Context context) {
        super(context);
        init();
    }

    public BaseUiWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseUiWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseUiWidget(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        inflate(getContext(), getLayoutId(), this);

        // Check if edit-mode is active:
        if (isInEditMode() && useSpecialPostInflateInEditMode()) {
            onPostInflateInEditMode();
        }
        else {
            onPostInflate();
        }
    }

    /**
     * If true, then {@link #onPostInflateInEditMode()} will be called when inflating the
     * view in edit-mode, which is active when using the Design View
     * when developing views.
     *
     * @return FALSE by default.
     */
    protected boolean useSpecialPostInflateInEditMode() {
        return false;
    }

    /**
     * Method called after inflating the layout while in edit mode.
     * See {@link #isInEditMode()}.
     */
    protected void onPostInflateInEditMode() {
        // Child classes may override.
    }

    protected abstract void onPostInflate();

    @LayoutRes
    protected abstract int getLayoutId();

}
