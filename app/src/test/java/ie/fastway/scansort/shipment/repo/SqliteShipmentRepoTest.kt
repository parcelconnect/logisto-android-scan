package ie.fastway.scansort.shipment.repo

import com.tubbert.powdroid.logging.UnitTestingTree
import com.tubbert.powdroid.mocking.Fakist
import ie.fastway.scansort.mocking.ShipmentMocker
import ie.fastway.scansort.shipment.Shipment
import ie.logistio.equinox.Equinox
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 */
@RunWith(RobolectricTestRunner::class)
class SqliteShipmentRepoTest {

    lateinit private var dbHelper: ShipmentDbOpenHelper

    lateinit private var shipmentRepo: SqliteShipmentRepo

    lateinit private var shipmentMocker: ShipmentMocker

    lateinit private var firstShipment: Shipment

    lateinit private var allShipmentsToStore: LinkedList<Shipment>

    lateinit private var latch: CountDownLatch

    lateinit private var fakist: Fakist

    /**
     * The last shipment that was found.
     */
    private var foundShipment: Shipment? = null

    @Before
    fun setUp() {
        Timber.plant(UnitTestingTree)
        Timber.d("Planted UnitTestingTree")

        foundShipment = null
        fakist = Fakist()

        shipmentMocker = ShipmentMocker()

        dbHelper = ShipmentDbOpenHelper(RuntimeEnvironment.application)
        dbHelper.clearAndRecreateDb()

        shipmentRepo = SqliteShipmentRepo(RuntimeEnvironment.application)

        firstShipment = Shipment(
                fakist.nextId(),
                "TEST-SqliteShipmentRepoTest-TRACKING_NUMBER",
                "TEST-SqliteShipmentRepoTest-BARCODE",
                "CF-101",
                "RF-901",
                fakist.nextAddress(),
                Equinox.nowSecond()
        )

        allShipmentsToStore = LinkedList()
        allShipmentsToStore.add(firstShipment)

        latch = CountDownLatch(1)
    }

    @After
    fun tearDown() {
        Timber.uprootAll()

        dbHelper.clearAllTables()
        ShipmentDbOpenHelper.clearSingleton()
        foundShipment = null
    }

    //----------------------------------------------------------------------------------------------

    @Test
    fun itCanInsertIntoDb() {
        // This will fail if the SQLite cannot store shipments.
        givenShipmentsAreStored()
    }

    @Test
    fun itCanFindByTrackingNumber() {
        givenShipmentsAreStored()

        seeBarcodeSearchFinds(firstShipment)
    }

    /*
    @Test
    fun itCanMigrateFromV1ToV2() {
    }
    */

    /**
     * Storing two shipments with the same tracking number should replace the old one.
     */
    @Test
    fun itUpdatesOnDuplicateTrackingNumber() {

        givenShipmentsAreStored()

        val alteredShipment = firstShipment.copy(
                id = firstShipment.id + 1000,
                updatedAt = firstShipment.updatedAt.plusMillis(5000)
        )

        shipmentRepo.storeAll(listOf(alteredShipment))

        seeBarcodeSearchFinds(alteredShipment)
    }

    @Test
    fun testRetrieveCorrectLatestShipment() {
        // Store the shipment at the current time:
        givenShipmentsAreStored()

        // Make a shipment updated in year 2022
        val shipment2022 = Shipment(
                fakist.nextId(),
                "TEST-SqliteShipmentRepoTest-TN-shipment2022",
                "TEST-SqliteShipmentRepoTest-BARCODE-shipment2022",
                "CF-122",
                "RF-922",
                fakist.nextAddress(),
                Equinox.convertTimestampToInstant("2022-01-01 09:00:00")
        )

        // Make a shipment updated in year 2032
        val shipment2032 = Shipment(
                fakist.nextId(),
                "TEST-SqliteShipmentRepoTest-TN-shipment2032",
                "TEST-SqliteShipmentRepoTest-BARCODE-shipment2032",
                "CF-132",
                "RF-932",
                fakist.nextAddress(),
                Equinox.convertTimestampToInstant("2032-01-01 09:00:00")
        )

        allShipmentsToStore.addAll(arrayOf(shipment2032, shipment2022))

        // Store the new shipments in the repo:
        givenShipmentsAreStored()

        seeLatestShipment(shipment2032)

    }

    /**
     * If we request a tracking number that doesn't exist, the callback should be
     * called with foundShipment=null.
     */
    @Test
    fun testRequestNonExistingShipment() {
        givenShipmentsAreStored()

        val nonExistingTrackingNumber = "TRACKING_NUMBER_testRequestNonExistingShipment"
        var foundTrackingNumber: String? = null
        shipmentRepo.findShipmentByBarcodeAsync(
                nonExistingTrackingNumber) { trackingNumber, foundShipment ->

            // A null value should be returned to the listener if no shipment was found.
            assertThat(foundShipment, nullValue())

            foundTrackingNumber = trackingNumber

            latch.countDown()
        }
        waitForLatch()
        assertThat(foundTrackingNumber, equalTo(nonExistingTrackingNumber))
    }

    @Test
    fun itCanPurgeExpiredShipments() {
        allShipmentsToStore.clear()

        // Dis-similar numbers are used to make debugging slightly more inuitive.

        val now = Equinox.now()
        addShipmentAtTime(now.toString(), now)

        val tomorrow = now.plus(1, ChronoUnit.DAYS)
        addShipmentAtTime(tomorrow.toString(), tomorrow)

        val yesterday = now.minus(1, ChronoUnit.DAYS)
        addShipmentAtTime(yesterday.toString(), yesterday)

        val threeDaysAgo = now.minus(3, ChronoUnit.DAYS)
        addShipmentAtTime(threeDaysAgo.toString(), threeDaysAgo)

        val sixDaysAgo = now.minus(6, ChronoUnit.DAYS)
        addShipmentAtTime(sixDaysAgo.toString(), sixDaysAgo)


        val sevenDaysAgo = now.minus(7, ChronoUnit.DAYS).minus(1, ChronoUnit.MINUTES)
        addShipmentAtTime(sevenDaysAgo.toString(), sevenDaysAgo)

        val tenDaysAgo = now.minus(10, ChronoUnit.DAYS)
        addShipmentAtTime(tenDaysAgo.toString(), tenDaysAgo)

        givenShipmentsAreStored()

        // All the seeded data should be in the repo:
        seeBarcodeIsInRepo(now.toString())
        seeBarcodeIsInRepo(tenDaysAgo.toString())

        // Purge old shipment data from the repo.
        shipmentRepo.purgeExpiredData()

        // The data that is less than 7 days old should still be in the repo:
        seeBarcodeIsInRepo(now.toString())
        seeBarcodeIsInRepo(tomorrow.toString())
        seeBarcodeIsInRepo(threeDaysAgo.toString())
        seeBarcodeIsInRepo(sixDaysAgo.toString())

        // The data that is older than 7 days should not be in the repo:
        seeBarcodeIsNotInRepo(sevenDaysAgo.toString())
        seeBarcodeIsNotInRepo(tenDaysAgo.toString())

    }

    private fun addShipmentAtTime(barcode: String, time: Instant) {
        val shipmentToStore = shipmentMocker.createShipment(updatedAt = time, barcode = barcode)
        allShipmentsToStore.add(shipmentToStore)
    }

    //----------------------------------------------------------------------------------------------

    private fun givenShipmentsAreStored() {
        shipmentRepo.storeAll(allShipmentsToStore)
    }

    private fun seeBarcodeIsInRepo(barcodeToFind: String) {
        shipmentRepo.findShipmentByBarcodeAsync(barcodeToFind) { barcode: String, foundShipment: Shipment? ->
            assertThat("barcodeToFind=$barcodeToFind;", foundShipment, notNullValue())
            latch.countDown()
        }
    }

    private fun seeBarcodeIsNotInRepo(barcodeToFind: String) {
        shipmentRepo.findShipmentByBarcodeAsync(barcodeToFind) { barcode: String, foundShipment: Shipment? ->
            assertThat(foundShipment, nullValue())
            latch.countDown()
        }
    }

    private fun seeBarcodeSearchFinds(shipmentToFind: Shipment) {

        shipmentRepo.findShipmentByBarcodeAsync(
                firstShipment.barcode, { repoBarcode, repoShipment ->

            foundShipment = repoShipment

            assertThat(repoShipment, notNullValue())

            assertThat(shipmentToFind.barcode, equalTo(repoBarcode))
            assertThat(shipmentToFind.barcode, equalTo(repoShipment!!.barcode))

            assertThat(shipmentToFind.trackingNumber, equalTo(repoShipment!!.trackingNumber))

            assertThat(shipmentToFind, equalTo(repoShipment))

            latch.countDown()
        })

        assertTrue("Latch did not count down", latch.await(100, TimeUnit.MILLISECONDS))

        assertNotNull(foundShipment)
    }

    private fun seeLatestShipment(expectedShipment: Shipment) {
        shipmentRepo.findLatestShipment { latestShipment ->
            foundShipment = latestShipment

            assertThat(latestShipment, notNullValue())
            latestShipment!!

            assertThat(latestShipment.trackingNumber, equalTo(expectedShipment.trackingNumber))
            assertThat(latestShipment.address, equalTo(expectedShipment.address))

            assertThat(latestShipment, equalTo(expectedShipment))

            latch.countDown()
        }
        waitForLatch()
        assertNotNull(foundShipment)
    }

    private fun waitForLatch() {
        assertTrue("Latch did not count down", latch.await(100, TimeUnit.MILLISECONDS))
    }

}