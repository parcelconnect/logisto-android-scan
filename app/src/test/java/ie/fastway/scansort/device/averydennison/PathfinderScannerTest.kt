package ie.fastway.scansort.device.averydennison

import avd.api.core.BarcodeType
import avd.api.core.IDevice
import avd.api.scanners.ScannerSe4500
import com.tubbert.powdroid.logging.UnitTestingTree
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.scanner.ScanEvent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import timber.log.Timber
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 */
class PathfinderScannerTest {

    lateinit var avdDevice :IDevice

    lateinit var pathfinderScanner: PathfinderScanner

    lateinit var receivedScanEvents: MutableList<ScanEvent>

    @Before
    fun setUp() {
        Timber.plant(UnitTestingTree)

        receivedScanEvents = LinkedList()

        // Create a mock Pathfinder:
        avdDevice = Mockito.mock(IDevice::class.java)
        Mockito.`when`(avdDevice.scanner).thenReturn(Mockito.mock(ScannerSe4500::class.java))

        pathfinderScanner = PathfinderScanner(ConnectableDevice.Factory.createMock(), avdDevice)
        pathfinderScanner.setScanEventListener(this::onScanEventBroadcast)
    }

    @Test
    fun it_debounces_scan_events() {
        val scanValue = "LG1234RTY"
        val barcodeType = BarcodeType.Code128

        pathfinderScanner.onScanReceived(scanValue, barcodeType, avdDevice)
        pathfinderScanner.onScanReceived(scanValue, barcodeType, avdDevice)
        pathfinderScanner.onScanReceived(scanValue, barcodeType, avdDevice)

        assertEquals(1, receivedScanEvents.size)

        // The first scan received later should pass:
        val latch = CountDownLatch(1)
        latch.await(500L, TimeUnit.MILLISECONDS)
        pathfinderScanner.onScanReceived(scanValue, barcodeType, avdDevice)
        assertEquals(2, receivedScanEvents.size)

        // A different scan received immediately should pass:
        pathfinderScanner.onScanReceived(scanValue + "999", barcodeType, avdDevice)
        assertEquals(3, receivedScanEvents.size)
    }

    @Test
    fun itIgnoresAllBarcodesExceptCode128() {
        val scanValue = "LG1234RTY"

        assertEquals(0, receivedScanEvents.size)

        for (barcodeType in BarcodeType.values()) {
            if(barcodeType != BarcodeType.Code128) {
                pathfinderScanner.onScanReceived(scanValue, barcodeType, avdDevice)
                assertEquals(0, receivedScanEvents.size)
            }
        }

        pathfinderScanner.onScanReceived(scanValue + "c", BarcodeType.Code128, avdDevice)
        assertEquals(1, receivedScanEvents.size)

    }

    private fun onScanEventBroadcast(scanEvent: ScanEvent) {
        receivedScanEvents.add(scanEvent)
    }

}