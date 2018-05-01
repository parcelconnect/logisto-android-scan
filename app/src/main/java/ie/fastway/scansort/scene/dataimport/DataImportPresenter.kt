package ie.fastway.scansort.scene.dataimport

import ie.fastway.scansort.scene.BaseScenePresenter
import ie.fastway.scansort.scene.MainSceneCoordinator
import ie.fastway.scansort.scene.SceneView

/**
 *
 */
class DataImportPresenter(sceneView: SceneView) : BaseScenePresenter(sceneView) {

    override fun onEnterScene() {

    }

    override fun getName() = MainSceneCoordinator.SceneKey.AUTH_SETUP

}