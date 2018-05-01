package ie.fastway.scansort.scene

import android.os.Bundle
import com.tubbert.powdroid.activity.BaseMajorActivity
import ie.fastway.scansort.R
import ie.fastway.scansort.device.DeviceConnector
import ie.fastway.scansort.config.AppConfig
import ie.fastway.scansort.lifecycle.AppSessionService
import ie.fastway.scansort.views.networkinfo.NetworkingInfoBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMajorActivity() {

    lateinit private var appContext: AppSessionContext
    lateinit private var appSessionService: AppSessionService
    lateinit private var sceneCoordinator: MainSceneCoordinator

    private lateinit var sceneContainer: SceneContainer
    private lateinit var networkInfoBar: NetworkingInfoBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind Views:
        sceneContainer = scene_container
        networkInfoBar = main_networkingInfo

        if (AppConfig.USE_MOCK_BLUETOOTH) {
            setupMockBluetoothMode()
        }

        // Create the App assetProvider:
        appContext = AppSessionContext(this)

        // Setup the scenes:
        sceneCoordinator = MainSceneCoordinator(appContext, sceneContainer)
        sceneCoordinator.onSessionStarted()
        backButtonPublisher.subscribe(sceneCoordinator)

        // Initialise the session:
        appSessionService = AppSessionService(appContext, sceneCoordinator)
        appSessionService.setupInfoBar(networkInfoBar)

        // Everything is initialised, start the session.
        appSessionService.start()

    }

    private fun setupMockBluetoothMode() {
        DeviceConnector.Factory.initiateMockBluetoothMode()
    }

    override fun onBackPressed() {
        if(!backButtonPublisher.onBackButtonPressed()) {
            // No listeners handled the back button event, so just hide this activity.
            moveTaskToBack(true)
        }

    }


}
