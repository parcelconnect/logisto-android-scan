package ie.fastway.scansort.scene

import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.view.View
import com.tubbert.powdroid.activity.OnBackPressedListener
import ie.fastway.scansort.device.averydennison.PathfinderConfigPresenter
import ie.fastway.scansort.device.view.DevicePairingPresenter
import ie.fastway.scansort.device.view.DevicePairingSceneView
import ie.fastway.scansort.lifecycle.AppSessionProvider
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.scanning.view.ScanningScenePresenter
import ie.fastway.scansort.scanning.view.ScanningSceneView
import ie.fastway.scansort.scene.dataimport.DataImportPresenter
import ie.fastway.scansort.scene.dataimport.DataImportSceneView
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

/**
 * Controls which scene is displayed at any given time in the MainActivity.
 *
 */
class MainSceneCoordinator(
        appSessionContext: AppSessionContext,
        val container: SceneContainer
) : OnBackPressedListener {

    private val dataImportPresenter: DataImportPresenter
    private val devicePairingPresenter: DevicePairingPresenter
    private val scanningScenePresenter: ScanningScenePresenter
    private val pathfinderConfigPresenter: PathfinderConfigPresenter

    private var currentScene: ScenePresenter? = null
    private var currentTransitionTask: SceneTransitionTask? = null


    init {
        // Data Import Scene ----
        val dataImportView = DataImportSceneView(container.getContainerView())
        dataImportPresenter = DataImportPresenter(dataImportView)
        dataImportView.setPresenter(dataImportPresenter)

        // Device Pairing Scene ----
        val devicePairingView = DevicePairingSceneView(container.getContainerView())
        devicePairingPresenter = DevicePairingPresenter(
                devicePairingView,
                appSessionContext.deviceManager,
                eventBus = appSessionContext.eventBus)
        devicePairingView.setPresenter(devicePairingPresenter)
        appSessionContext.eventBus.register(devicePairingPresenter)

        // Scanning Scene ----
        val scanningSceneView = ScanningSceneView(container.getContainerView())
        scanningScenePresenter = ScanningScenePresenter(
                scanningSceneView, appSessionContext.deviceManager)
        scanningSceneView.setPresenter(scanningScenePresenter)
        appSessionContext.eventBus.register(scanningScenePresenter)
        scanningScenePresenter.onLaunchDeviceConfigListener = {
            showScene(SceneKey.PATHFINDER_CONFIG)
        }

        // Pathfinder Config Scene ----
        pathfinderConfigPresenter = PathfinderConfigPresenter.Factory
                .create(container,
                        appSessionContext.deviceManager,
                        onExitListener = this::exitPathfinderConfig
                )
    }

    fun onSessionStarted() {
        showScene(SceneKey.AUTH_SETUP)
    }

    fun showScanningScene() {
        showScene(SceneKey.SCANNING)
    }

    /**
     * Called when the auth credentials have been setup correctly.
     * Indicates that the "Bluetooth Pairing" scene should be shown.
     */
    fun showDevicePairingScene() {
        showScene(SceneKey.DEVICE_PAIRING)
    }

    private fun exitPathfinderConfig() {
        showScene(SceneKey.SCANNING)
    }

    private fun showScene(sceneKey: String) {

        if (LogConfig.APP_SESSION) {
            Timber.d("Showing scene: $sceneKey")
        }

        val scenePresenter = when (sceneKey) {

            SceneKey.AUTH_SETUP -> dataImportPresenter

            SceneKey.DEVICE_PAIRING -> devicePairingPresenter

            SceneKey.SCANNING -> scanningScenePresenter

            SceneKey.PATHFINDER_CONFIG -> pathfinderConfigPresenter

            else -> {
                throw IllegalArgumentException("Invalid SceneKey:<$sceneKey>")
            }
        }

        if (LogConfig.SCENES) {
            Timber.d("Going to scene: " + scenePresenter.getName())
        }

        synchronized(this) {
            // Ensure that any pending transition has expired.
            currentTransitionTask?.expire()

            val transitionTask = SceneTransitionTask(scenePresenter)
            transitionTask.executeTransition()

            currentScene = scenePresenter
        }
    }

    override fun onBackButtonPressed(): Boolean {
        return if (SceneKey.PATHFINDER_CONFIG.equals(currentScene?.getName())) {
            showScene(SceneKey.SCANNING)
            true
        }
        else {
            false
        }
    }

    //----------------------------------------------------------------------------------------------

    inner class SceneTransitionTask(private val scenePresenter: ScenePresenter)
        : Transition.TransitionListener {

        private val transition: Transition
        private val logTag = scenePresenter.getName()

        var isTriggered = false
        var isStarted = false
        var isPaused = false
        var isResumed = false
        var isFinished = false
        var isCancelled = false
        var isExpired = false

        init {
            transition = AutoTransition()
            transition.addListener(this)
        }

        fun executeTransition() {
            AppSessionProvider.createSingleTaskScheduler(100)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (LogConfig.SCENES) {
                            Timber.d("$logTag; Triggering transition.")
                        }

                        if (shouldTriggerTransition()) {
                            isTriggered = true
                            TransitionManager.go(scenePresenter.getView().getScene(), transition)
                            scheduleTransitionWatcher()
                        }
                    }
        }

        /**
         * Starts a scheduled task that will ensure that the transition gets executed.
         */
        private fun scheduleTransitionWatcher() {
            AppSessionProvider.createSingleTaskScheduler(600)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (mayExecuteTransition()) {
                            // Double-checked locking.
                            synchronized(this) {
                                if (mayExecuteTransition()) {
                                    // The Transition framework is fairly unreliable, and some
                                    // transitions just never execute correctly.
                                    // If the scene is meant to show but for some reason it hasn't
                                    // then we force it to be visible here.

                                    if (LogConfig.SCENES) {
                                        Timber.d("$logTag; TransitionWatcher forcing scene to be visible. TransitionTask=$this")
                                    }

                                    scenePresenter.getView().getRootView().visibility = View.VISIBLE
                                    scenePresenter.getView().getScene().enter()
                                }
                            }
                        }
                    }
        }

        override fun onTransitionStart(transition: Transition) {
            if (LogConfig.SCENES) {
                Timber.d("$logTag; onTransitionStart")
            }

            isStarted = true
        }

        override fun onTransitionPause(transition: Transition) {
            if (LogConfig.SCENES) {
                Timber.d("$logTag; onTransitionPause")
            }

            isPaused = true
            isResumed = false
        }

        override fun onTransitionResume(transition: Transition) {
            if (LogConfig.SCENES) {
                Timber.d("$logTag; onTransitionResume")
            }

            isPaused = false
            isResumed = true
        }

        override fun onTransitionEnd(transition: Transition) {
            if (LogConfig.SCENES) {
                Timber.d("$logTag; onTransitionEnd")
            }

            isFinished = true
        }

        override fun onTransitionCancel(transition: Transition) {
            if (LogConfig.SCENES) {
                Timber.d("$logTag; onTransitionCancel")
            }

            isCancelled = true
        }

        fun expire() {
            if (LogConfig.SCENES) {
                Timber.d("exiring transition task: ${this}.")
            }

            isExpired = true
        }

        private fun shouldTriggerTransition() =
                ((!isTriggered) && mayExecuteTransition())

        private fun mayExecuteTransition() =
                (!(isExpired || isCancelled || isFinished || isStarted))

        override fun toString(): String {
            return "SceneTransitionTask(logTag='$logTag', isTriggered=$isTriggered, isStarted=$isStarted, isPaused=$isPaused, isResumed=$isResumed, isFinished=$isFinished, isCancelled=$isCancelled)"
        }

    }

    /**
     * Named keys used to indicate the scenes to show.
     */
    public object SceneKey {
        const val AUTH_SETUP = "Scene.AUTH_SETUP"
        const val DEVICE_PAIRING = "Scene.DEVICE_PAIRING"
        const val SCANNING = "Scene.SCANNING"
        const val PATHFINDER_CONFIG = "Scene.PATHFINDER_CONFIG"
    }


}