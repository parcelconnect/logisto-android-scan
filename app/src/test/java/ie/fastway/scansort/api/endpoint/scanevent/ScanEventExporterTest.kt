package ie.fastway.scansort.api.endpoint.scanevent

import com.tubbert.powdroid.logging.UnitTestingTree
import ie.fastway.scansort.api.ApiEndpointTester
import ie.fastway.scansort.device.mocking.ScanMocker
import ie.logistio.equinox.Equinox
import ie.logistio.paloma.mock.ApiServiceMocker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 *
 */
class ScanEventExporterTest {

    lateinit private var apiMocker: ScanEventLogApiMocker
    lateinit private var api: ScanEventLogApi
    lateinit private var scanEventExporter: ScanEventExporter
    lateinit private var scanMocker: ScanMocker
    lateinit private var endpointTester: ApiEndpointTester

    @Before
    fun setUp() {
        Timber.plant(UnitTestingTree)
        Timber.d("TESTING TREE")

        endpointTester = ApiEndpointTester()

        apiMocker = ScanEventLogApiMocker()
        apiMocker.setEnqueueExecutor(ApiServiceMocker.SyncronousExecutor())
        api = apiMocker.createMockApi()

        scanEventExporter = ScanEventExporter(api)
        scanMocker = ScanMocker()
    }

    @Test
    fun itCanExportToApi() {
        givenScansAreExported(11)
        seeJobsInApi(1)

        givenScansAreExported(19)
        seeJobsInApi(19)
    }

    @Test
    fun testApiExportFormat() {

        val expectedTimestamp = "2017-03-15 11:12:13"
        scanMocker.eventAtOverride = Equinox.convertTimestampToInstant(expectedTimestamp)
        givenScansAreExported(11)

        val eventJsonArray = getPostedScanEvents()
        assertNotNull(eventJsonArray)

        eventJsonArray!!.forEach { eventJson ->
            assertEquals(expectedTimestamp, eventJson.eventAt)
        }

    }

    @Test
    fun itRetriesToSendLogsOnNetworkError() {

        val scanResult = scanMocker.createMockScanResult()
        scanEventExporter.onScanResult(scanResult)

        apiMocker.queueNetworkError()

        whenExportIsScheduledImmediately()

        waitForScheduledExport()

        scanEventExporter.stopExportSchedule()

        // It should have tried to send one job:
        seeJobsInApi(1)

        val scanResult2 = scanMocker.createMockScanResult()
        scanEventExporter.onScanResult(scanResult2)

        whenExportIsScheduledImmediately()
        waitForScheduledExport()

        // The first job should have been re-sent with the first one.
        seeJobsInApi(2)

    }

    @Test
    fun itDoesntSendSameRequestTwice() {
        val expectedSize = 5
        givenThereAreScansInTheQueue(expectedSize)

        whenExportIsScheduledImmediately()
        waitForScheduledExport()
        scanEventExporter.stopExportSchedule()

        seeJobsInApi(expectedSize)

        apiMocker.clearRequestHistory()

        // Schedule another export; no jobs should be sent this time.
        whenExportIsScheduledImmediately()
        waitForScheduledExport()
        seeJobsInApi(0)
    }

    //----------------------------------------------------------------------------------------------

    private fun givenScansAreExported(count: Int) {
        givenThereAreScansInTheQueue(1)

        whenExportIsScheduledImmediately()

        waitForScheduledExport()
    }

    private fun givenThereAreScansInTheQueue(count: Int) {
        for (i in 1..count) {
            scanEventExporter.onScanResult(scanMocker.createMockScanResult())
        }
    }

    private fun whenExportIsScheduledImmediately() {
        scanEventExporter.startExportSchedule(initialDelayMs = 0)
    }

    private fun waitForScheduledExport() {
        endpointTester.countdownLatch.await(50, TimeUnit.MILLISECONDS)
    }

    private fun seeJobsInApi(count: Int) {
        val expectedSize = if (count == 0) null else count;

        assertEquals(expectedSize, getPostedScanEvents()?.size)
    }

    private fun getPostedScanEvents() = apiMocker.lastRequest?.scanEvents


}