package ie.fastway.scansort.scene

import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber

/**
 *
 */
abstract class BaseScenePresenter(open val sceneView: SceneView)
    : ScenePresenter {

    override fun getView(): SceneView {
        return sceneView
    }

    override fun onSceneInflated() {
        if (LogConfig.SCENES) {
            Timber.v("onSceneInflated; sceneName=${getName()};")
        }
        // To be overriden if needed.
    }

    override fun onExitScene() {}

}