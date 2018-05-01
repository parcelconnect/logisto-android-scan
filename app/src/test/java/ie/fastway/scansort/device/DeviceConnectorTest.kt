package ie.fastway.scansort.device

import com.tubbert.powdroid.android.context.MockAssetProvider
import ie.fastway.scansort.device.pairing.MockBluetoothConnector
import ie.fastway.scansort.device.pairing.ScanningDeviceRegistry
import ie.fastway.scansort.lifecycle.EventPublisher
import org.junit.Before

/**
 *
 */
class DeviceConnectorTest {

    private lateinit var bluetoothConnector: MockBluetoothConnector
    private lateinit var deviceConnector: DeviceConnector
    private lateinit var deviceRegistry: ScanningDeviceRegistry
    private lateinit var eventPublisher: EventPublisher

    @Before
    fun setUp() {
        deviceRegistry = ScanningDeviceRegistry()
        bluetoothConnector = MockBluetoothConnector()
        bluetoothConnector.connectableDeviceRegistry = deviceRegistry
        eventPublisher = EventPublisher()
        deviceConnector = DeviceConnector(MockAssetProvider(), bluetoothConnector, deviceRegistry, eventPublisher)
    }

    /*
    @Test
    fun it_can_resolve_pathfinder_and_symbol() {
        deviceConnector.
        // TODO: Inject mock bluetooth devices.
    }
    */
}