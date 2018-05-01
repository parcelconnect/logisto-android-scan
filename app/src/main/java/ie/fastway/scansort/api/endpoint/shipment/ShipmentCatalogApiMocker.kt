package ie.fastway.scansort.api.endpoint.shipment

import com.tubbert.powdroid.collections.ForeverIterator
import com.tubbert.powdroid.mocking.Fakist
import ie.fastway.scansort.logging.LogConfig
import ie.logistio.equinox.Equinox
import ie.logistio.paloma.mock.ApiServiceMocker
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 */
class ShipmentCatalogApiMocker : ApiServiceMocker<ShipmentCatalogApi>() {

    private val fakist = Fakist()

    private val seededTrackingNumbers = ArrayList<String>()

    private var trackingNumberIterator = ForeverIterator(seededTrackingNumbers)

    var countToSeed = 2
        set(value) {
            if (value < 0)
                throw IllegalArgumentException("countToSeed must be positive.")
            else
                field = value
        }

    override fun createMockApi(): ShipmentCatalogApi {

        return ShipmentCatalogApi {
            val responseJson = ShipmentCatalogResponseJson.createEmpty()

            with(responseJson.data) {
                cursorDatetime = Equinox.nowMysqlDatetime()

                shipments = createShipmentsJsonArray()

                totalRetrievable = shipments.size
                totalRetrieved = shipments.size
                isPaginated = false
            }

            if (LogConfig.SHIPMENTS) {
                Timber.d("Created ${responseJson.data?.shipments?.size} mock ShipmentJsons")
            }

            createApiCall(responseJson)
        }
    }

    private fun createShipmentsJsonArray(): List<ShipmentJson> {
        val shipmentJsons = LinkedList<ShipmentJson>();
        for (i in 0..countToSeed) {
            val shipmentJson = ShipmentJson()

            with(shipmentJson) {
                id = fakist.nextId()

                trackingNumber = fakist.nextTrackingNumber()
                seededTrackingNumbers.add(trackingNumber)

                // Make the barcode and tracking number different:
                barcode = fakist.makeFakeString(2) + trackingNumber

                address = "15 Allday Way, Villardio, Sao Paulo"
                courierFranchisee = fakist.createNumericString(3)
                regionalFranchisee = fakist.chooseFrom(*REGIONS)
                updatedAt = Equinox.now()
            }

            shipmentJsons.add(shipmentJson)

            if (LogConfig.SHIPMENT_MOCKER && i % 1000 == 0) {
                Timber.d("ShipmentJson count=${shipmentJsons.size}")
            }
        }
        return shipmentJsons
    }

    override fun getType(): Class<ShipmentCatalogApi>? {
        return ShipmentCatalogApi::class.java
    }

    fun getSeededTrackingNumber(): String? {
        if (LogConfig.SHIPMENT_MOCKER) {
            Timber.d("getSeededTrackingNumber; seededTrackingNumbers.size=${seededTrackingNumbers.size}")
        }

        return if (seededTrackingNumbers.isNotEmpty() && trackingNumberIterator.hasNext()) {
            trackingNumberIterator.next()
        }
        else null
    }


    companion object {
        val REGIONS = arrayOf("SWE", "DUB", "NWS", "MID", "CRK")
    }

}
