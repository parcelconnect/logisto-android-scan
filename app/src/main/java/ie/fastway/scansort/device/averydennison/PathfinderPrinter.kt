package ie.fastway.scansort.device.averydennison

import avd.api.core.IDevice
import avd.api.core.IPrinter
import avd.api.core.exceptions.ApiPrinterException
import avd.api.printers.Printer6140
import com.tubbert.powdroid.android.context.AssetProvider
import ie.fastway.scansort.device.ConnectableDevice
import ie.fastway.scansort.device.printer.LabelMessage
import ie.fastway.scansort.device.printer.LabelPrinter
import ie.fastway.scansort.logging.LogConfig
import ie.logistio.equinox.Equinox
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * The AveryDennison Monarch Pathfinder printer.
 */
class PathfinderPrinter(
        assetProvider: AssetProvider,
        private val connectableDevice: ConnectableDevice,
        private val avdDevice: IDevice
) : LabelPrinter {

    private val avdPrinter: IPrinter
        get() = avdDevice.printer

    private val printer6140: Printer6140
        get() = avdPrinter as Printer6140

    private val resourceHelper = PathfinderResourceHelper(assetProvider)

    init {
        resourceHelper.initialiseResources()
    }

    override fun print(label: LabelMessage) {
        Schedulers.io().scheduleDirect {
            when (label.labelType) {

                LabelType.SHIPMENT -> printShipmentLabel(label)

                LabelType.UNKNOWN -> printUnknownLabel(label)

                else -> printGenericLabel(label)
            }
        }
    }

    private fun printShipmentLabel(label: LabelMessage) {

        val rf = label.primaryText ?: "-"
        val cf = label.secondaryText ?: "-"

        val now = Equinox.convertToSqlFormat(label.timestamp, "")

        val instructionData = arrayOf<ByteArray>(
                rf.toByteArray(),
                cf.toByteArray(),
                now.toByteArray())

        printViaLnt(PathfinderResourceHelper.LNT_SHIPMENT_LABEL_ALIAS, instructionData)
    }

    private fun printUnknownLabel(label: LabelMessage) {
        // TODO: Print a special Error label.
        if (LogConfig.AVD_PRINTER) {
            Timber.d("TODO: printErrorLabel not implemented. label=$label")
        }
        val notFoundMessage = "N/A"
        val secondaryMessage = ""
        val now = Equinox.convertToSqlFormat(label.timestamp, "")

        val instructionData = arrayOf<ByteArray>(
                notFoundMessage.toByteArray(),
                secondaryMessage.toByteArray(),
                now.toByteArray()
        )

        printViaLnt(PathfinderResourceHelper.LNT_SHIPMENT_LABEL_ALIAS, instructionData)
    }

    private fun printGenericLabel(label: LabelMessage) {
        // TODO: Print a special Generic label.
        if (LogConfig.AVD_PRINTER) {
            Timber.d("TODO: printGenericLabel not implemented. label=$label")
        }
    }

    /**
     * Prints to a label stored as an LNT resource on the Pathfinder.
     */
    private fun printViaLnt(lntAlias: String, labelFields: Array<ByteArray>) {

        try {
            avdPrinter.print(lntAlias, 1, labelFields)
        }
        catch (e: ApiPrinterException) {
            if (LogConfig.AVD_PRINTER) {
                Timber.e(e, "Unable to print with LNT. errorCode=${e.errorCode}; alias=$lntAlias;")
            }
        }

    }


}