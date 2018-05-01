package ie.fastway.scansort.scene

import android.support.transition.Scene
import android.view.View

/**
 *
 */
interface SceneView {

    fun setPresenter(presenter: ScenePresenter)

    fun getScene(): Scene

    fun getRootView(): View

}