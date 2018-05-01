package ie.fastway.scansort.lifecycle


import ie.fastway.scansort.device.printer.LabelPrinter
import ie.fastway.scansort.device.scanner.ScanningDevice
import ie.logistio.equinox.Equinox
import ie.logistio.paloma.json.AuthCredentials
import org.threeten.bp.Instant


/**
 *
 */
data class AppSessionEvent<out T : AppSessionEvent.EventBundle>(
        val eventBundle: T,
        val timestamp: Instant
) {

    object Factory {

        fun onScannerConnected(connectedDevice: ScanningDevice)
                : AppSessionEvent<EventBundle.ScanningDeviceConnected> {

            return AppSessionEvent(
                    EventBundle.ScanningDeviceConnected(connectedDevice),
                    Equinox.now())
        }

        fun onScannerDisconnected(scanner: ScanningDevice)
                : AppSessionEvent<EventBundle.ScanningDeviceDiconnected> {

            return AppSessionEvent(
                    EventBundle.ScanningDeviceDiconnected(scanner),
                    Equinox.now()
            )
        }

        fun onPrinterConnected(connectedPrinter: LabelPrinter)
                : AppSessionEvent<EventBundle.LabelPrinterConnected> {

            return AppSessionEvent(
                    EventBundle.LabelPrinterConnected(connectedPrinter),
                    Equinox.now()
            )
        }

        fun onPrinterDisconnected(printer: LabelPrinter):
                AppSessionEvent<EventBundle.LabelPrinterDisonnected> {

            return AppSessionEvent(
                    EventBundle.LabelPrinterDisonnected(printer),
                    Equinox.now()
            )
        }

    }

    sealed class EventBundle {

        class AuthTokenAvailable(val auth: AuthCredentials) : EventBundle()

        class ScanningDeviceConnected(val scanningDevice: ScanningDevice) : EventBundle()

        class ScanningDeviceDiconnected(val scanningDevice: ScanningDevice) : EventBundle()

        class LabelPrinterConnected(val labelPrinter: LabelPrinter) : EventBundle()

        class LabelPrinterDisonnected(val labelPrinter: LabelPrinter) : EventBundle()

    }

}