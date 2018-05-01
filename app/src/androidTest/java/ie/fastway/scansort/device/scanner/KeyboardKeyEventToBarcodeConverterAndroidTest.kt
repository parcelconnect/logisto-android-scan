package ie.fastway.scansort.device.scanner

import android.os.SystemClock
import android.support.test.runner.AndroidJUnit4
import android.view.KeyCharacterMap
import android.view.KeyEvent
import com.tubbert.powdroid.logging.InstrumentationTestingTree
import ie.fastway.scansort.device.mocking.MockScanner
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 */
@RunWith(AndroidJUnit4::class)
class KeyboardKeyEventToBarcodeConverterAndroidTest {

    private lateinit var keyboardListener: KeyboardKeyEventToBarcodeConverter
    private lateinit var mockScanner: MockScanner
    private lateinit var latch: CountDownLatch

    private var latestScanEvent: ScanEvent? = null

    private val virtualKeyboard = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)

    @Before
    fun setUp() {
        Timber.plant(InstrumentationTestingTree)
        Timber.d("InstrumentationTestingTree has been planted.")

        latestScanEvent = null
        latch = CountDownLatch(1)
        keyboardListener = KeyboardKeyEventToBarcodeConverter()
        mockScanner = MockScanner()
        mockScanner.setScanEventListener(this::onScanEvent)
        keyboardListener.onBarcodeReadyCallback = mockScanner.barcodeScanCallback
    }

    @After
    fun tearDown() {
        Timber.uproot(InstrumentationTestingTree)
    }

    @Test
    fun it_notifies_listeners_when_barcode_is_complete() {
        val expectedBarcode = "PAULO90"
        val keyEvents = createKeyEvents(KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_L, KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_0)

        for (keyEvent in keyEvents) {
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                keyboardListener.onKeyDown(keyEvent.keyCode, keyEvent)
            }
            else {
                keyboardListener.onKeyUp(keyEvent.keyCode, keyEvent)
            }
        }
        val timeToWait = (KeyboardKeyEventToBarcodeConverter.SCHEDULE_SEQUENCE_CHECK_MS * 2) + 20
        assertTrue(latch.await(timeToWait, TimeUnit.MILLISECONDS))

        assertEquals(expectedBarcode, latestScanEvent?.scannedValue)
    }

    /**
     * Tests that it notifies listeners of barcodes beyong the first barcode that is read.
     *
     */
    @Test
    fun it_can_process_multiple_barcodes() {
        val firstBarcode = "ThisIsBarcode1984"
        givenBarcodeIsReceived(firstBarcode)
        whenWaitTimeHasExpired()
        thenSeeBarcodeWasReceived(firstBarcode)

        val secondBarcode = "OhLookAnotherBarcode2389"
        givenBarcodeIsReceived(secondBarcode)
        whenWaitTimeHasExpired()
        thenSeeBarcodeWasReceived(secondBarcode)
    }

    private fun givenBarcodeIsReceived(barcode: String) {
        val keyEvents = createKeyEvents(barcode)

        for (keyEvent in keyEvents) {
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                keyboardListener.onKeyDown(keyEvent.keyCode, keyEvent)
            }
            else {
                keyboardListener.onKeyUp(keyEvent.keyCode, keyEvent)
            }
        }
    }

    private fun whenWaitTimeHasExpired() {
        val timeToWait = (KeyboardKeyEventToBarcodeConverter.SCHEDULE_SEQUENCE_CHECK_MS * 2) + 20
        latch = CountDownLatch(1)
        assertTrue(latch.await(timeToWait, TimeUnit.MILLISECONDS))
    }

    private fun thenSeeBarcodeWasReceived(expectedBarcode: String) {
        assertEquals(expectedBarcode.toUpperCase(), latestScanEvent?.scannedValue)
    }

    private fun createKeyEvents(scannedMessage: String): List<KeyEvent> {
        val messageAsChars = scannedMessage.toCharArray()
        return virtualKeyboard.getEvents(messageAsChars).asList()
    }

    private fun createKeyEvents(vararg keyCodes: Int): List<KeyEvent> {
        val createdEvents = LinkedList<KeyEvent>()

        for (keyCode in keyCodes) {

            /* TO HANDLE UPPER AND LOWER CASE

            val isUpperCase = (keyCode == KeyEvent.KEYCODE_P)

            val metaState = if (isUpperCase) KeyEvent.META_SHIFT_ON else null

            val upEvent = isUpperCase ...

            val keyEvent = KeyEvent(0L, getUptime(), KeyEvent.ACTION_DOWN, keyCode, 0, metaState)

            val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            if (isUpperCase) {
                = KeyEvent.META_SHIFT_ON
            }
            */

            createdEvents.add(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            createdEvents.add(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
        return createdEvents
    }

    private fun getUptime(): Long {
        return SystemClock.uptimeMillis()
    }

    //----------------------------------------------------------------------------------------------

    fun onScanEvent(scanEvent: ScanEvent) {
        latestScanEvent = scanEvent
        latch.countDown()
    }

}