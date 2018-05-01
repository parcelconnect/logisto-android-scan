package ie.fastway.scansort.scene

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 *
 */
class FrameSceneContainer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SceneContainer {

    override fun getContainerView() = this

}