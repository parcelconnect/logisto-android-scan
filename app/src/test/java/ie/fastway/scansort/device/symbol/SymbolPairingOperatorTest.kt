package ie.fastway.scansort.device.symbol

import ie.fastway.scansort.device.ConnectableDevice
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 *
 */
class SymbolPairingOperatorTest{

    private lateinit var symbolOperator: SymbolPairingOperator

    @Before
    fun setUp() {
        symbolOperator = SymbolPairingOperator()
    }

    @Test
    fun it_can_recognise_symbol_scanner() {
        val scanner = ConnectableDevice.Factory.createMockSymbolScanner()

        assertTrue(symbolOperator.canPairWith(scanner));
    }
}