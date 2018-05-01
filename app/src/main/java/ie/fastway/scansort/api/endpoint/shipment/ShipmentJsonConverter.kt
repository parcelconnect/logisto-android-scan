package ie.fastway.scansort.api.endpoint.shipment

import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.shipment.Shipment
import timber.log.Timber

/**
 *
 */
class ShipmentJsonConverter {

    fun convertAll(shipmentJsons: Collection<ShipmentJson>)
            = shipmentJsons.map(this::convert)

    private fun convert(shipmentJson: ShipmentJson): Shipment {
        if (LogConfig.SHIPMENT_API) {
            Timber.d("Converting ShipmentJson:$shipmentJson")
        }

        return Shipment(
                id = shipmentJson.id,
                trackingNumber = shipmentJson.trackingNumber,
                barcode = shipmentJson.barcode,
                courierFranchisee = shipmentJson.courierFranchisee,
                regionalFranchisee = shipmentJson.regionalFranchisee,
                address = shipmentJson.address,
                updatedAt = shipmentJson.updatedAt
        )
    }


}