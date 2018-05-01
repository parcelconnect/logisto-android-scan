package ie.fastway.scansort.device.mocking

import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.scanner.KeyboardScanner

/**
 *
 */
class MockScanner(
        connectableDevice: ConnectableDevice = ConnectableDevice.Factory.createMock()
) : KeyboardScanner(connectableDevice) {

}