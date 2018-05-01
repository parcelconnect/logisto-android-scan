package ie.fastway.scansort.mocking

import com.tubbert.powdroid.mocking.Fakist
import ie.fastway.scansort.shipment.Shipment
import ie.logistio.equinox.Equinox
import org.threeten.bp.Instant

/**
 *
 */
class ShipmentMocker {

    private val fakist = Fakist()

    fun createShipment(
            updatedAt: Instant = Equinox.nowSecond(),
            trackingNumber: String = fakist.nextTrackingNumber(),
            barcode: String = trackingNumber
    ): Shipment {

        return Shipment(
                id = fakist.nextId(),
                trackingNumber = trackingNumber,
                barcode = barcode,
                courierFranchisee = fakist.makeFakeString(3),
                regionalFranchisee = fakist.makeFakeString(3),
                address = fakist.nextAddress(),
                updatedAt = updatedAt
        )
    }

}