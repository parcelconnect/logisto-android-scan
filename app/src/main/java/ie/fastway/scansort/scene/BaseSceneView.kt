package ie.fastway.scansort.scene

import android.support.annotation.LayoutRes
import android.support.transition.AutoTransition
import android.support.transition.Scene
import android.support.transition.TransitionManager
import android.view.ViewGroup
import ie.fastway.scansort.logging.LogConfig
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 */
abstract class BaseSceneView(val sceneRoot: ViewGroup) : SceneView {

    private val scene: Scene

    protected var scenePresenter: ScenePresenter? = null
    // var onEnterAction: () -> Unit = {}

    val hasBeenSetUp = AtomicBoolean(false)

    init {
        @Suppress("LeakingThis")
        scene = Scene.getSceneForLayout(sceneRoot, getLayoutId(), sceneRoot.context)

        scene.setEnterAction({
            if (LogConfig.SCENES) {
                Timber.v("onEnterScene; scene=" + this.javaClass.simpleName)
            }

            bindViews()

            scenePresenter?.let {
                if(!hasBeenSetUp.getAndSet(true)) {
                    it.onSceneInflated()
                }

                it.onEnterScene()
            }
        })
    }

    override fun setPresenter(presenter: ScenePresenter) {
        this.scenePresenter = presenter
    }

    protected fun beginDelayedTransition() {
        val transition = AutoTransition().setDuration(100)

        TransitionManager.beginDelayedTransition(sceneRoot, transition)
    }

    abstract fun bindViews()

    fun isSetUp(): Boolean = hasBeenSetUp.get()

    /**
     * WARNING: This is called before the child class is initialised.
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun getRootView() = sceneRoot

    override fun getScene() = scene


}