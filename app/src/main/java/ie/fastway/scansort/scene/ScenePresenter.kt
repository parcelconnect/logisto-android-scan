package ie.fastway.scansort.scene

/**
 *
 */
interface ScenePresenter {

    /**
     * Called when the underlying view for the Scene is inflated.
     * This should only be called once during the lifecycle of any given presenter.
     */
    fun onSceneInflated()

    /**
     * Called each time the scene is shown to the user.
     */
    fun onEnterScene()

    fun onExitScene()

    fun getView(): SceneView

    /**
     * Gets a name by which this scene will be refered.
     */
    fun getName(): String
}