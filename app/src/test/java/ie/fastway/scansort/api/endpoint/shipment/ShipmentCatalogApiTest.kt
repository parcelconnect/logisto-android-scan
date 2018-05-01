package ie.fastway.scansort.api.endpoint.shipment

import ie.fastway.scansort.api.ApiEndpointTester
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant

/**
 *
 */
class ShipmentCatalogApiTest {

    lateinit var apiTester: ApiEndpointTester
    lateinit var shipmentApi: ShipmentCatalogApi

    @Before
    @Throws(Exception::class)
    fun setUp() {
        apiTester = ApiEndpointTester()
    }

    @Test
    fun canCreateApiWithRetrofit() {

        shipmentApi = apiTester.apiService
                .createApi(ShipmentCatalogApi::class.java)

        assertThat(shipmentApi, instanceOf(ShipmentCatalogApi::class.java))
    }

    @Test
    fun canConvertWithGson() {

        val convertedJson = apiTester.gson.fromJson(
                Json.RESPONSE_OK, ShipmentCatalogResponseJson::class.java)

        assertThat(convertedJson.data,
                instanceOf(ShipmentCatalogResponseJson.BodyData::class.java))

        convertedJson.data.shipments.forEach {
            assertThat(it.id, greaterThanOrEqualTo(0L) )
            assertThat(it.trackingNumber, instanceOf(String::class.java) )
            assertThat(it.updatedAt, instanceOf(Instant::class.java))
        }


    }

    companion object Json {

        const val RESPONSE_OK = """
                    {
                        "data": {
                            "cursor_datetime": "2017-10-27 15:58:24",
                            "total_retrievable": 513,
                            "total_retrieved": 5,
                            "is_paginated": true,
                            "shipments": [
                                {
                                    "id": 286,
                                    "tracking_number": "NY0001690159",
                                    "courier_id": 46,
                                    "place_id": 234,
                                    "address": "O'HALLORAN ROAD, NATIONAL TECHNOLOGY PARK, LIMERICK, Limerick",
                                    "latitude": "52.676177",
                                    "longitude": "-8.551854",
                                    "courier_franchisee": "Greystones",
                                    "regional_franchisee": "SWE",
                                    "created_at": "2017-11-01 15:58:02",
                                    "updated_at": "2017-11-01 15:58:02",
                                    "deleted_at": null
                                },
                                {
                                    "id": 287,
                                    "tracking_number": "NY0001690160",
                                    "courier_id": 47,
                                    "place_id": 235,
                                    "address": "KAMRICK COURT, BALLYBRIT BUSINESS PARK, GALWAY, H91 XY38",
                                    "latitude": "53.290031",
                                    "longitude": "-9.010184",
                                    "courier_franchisee": "123-89",
                                    "regional_franchisee": "DUB",
                                    "created_at": "2017-11-01 15:58:02",
                                    "updated_at": "2017-11-01 15:58:02",
                                    "deleted_at": null
                                },
                                {
                                    "id": 288,
                                    "tracking_number": "NY0001690161",
                                    "courier_id": 48,
                                    "place_id": 236,
                                    "address": "UNIT 5, CARRIGALINE INDUSTRIAL PARK, CARRIGALINE, Cork",
                                    "latitude": "51.808403",
                                    "longitude": "-8.368735",
                                    "courier_franchisee": "West Killarney",
                                    "regional_franchisee": "CRK",
                                    "created_at": "2017-11-01 15:58:02",
                                    "updated_at": "2017-11-01 15:58:02",
                                    "deleted_at": null
                                },
                                {
                                    "id": 289,
                                    "tracking_number": "NY0001690163",
                                    "courier_id": 49,
                                    "place_id": 237,
                                    "address": "UNIT 2, OWENACURRA BUS. PARK, KNOCKGRIFFEN, MIDLETON, Cork",
                                    "latitude": "51.919042",
                                    "longitude": "-8.185823",
                                    "courier_franchisee": "9182",
                                    "regional_franchisee": "SWE",
                                    "created_at": "2017-11-01 15:58:02",
                                    "updated_at": "2017-11-01 15:58:02",
                                    "deleted_at": null
                                },
                                {
                                    "id": 290,
                                    "tracking_number": "NY0001690164",
                                    "courier_id": 50,
                                    "place_id": 238,
                                    "address": "BUTTEVANT ROAD, DROMCOLLIHER, Limerick",
                                    "latitude": "52.338603",
                                    "longitude": "-8.905222",
                                    "courier_franchisee": null,
                                    "regional_franchisee": "SWE",
                                    "created_at": "2017-11-01 15:58:02",
                                    "updated_at": "2017-11-01 15:58:02",
                                    "deleted_at": null
                                }
                            ]
                        }
                    }
            """

    }
}