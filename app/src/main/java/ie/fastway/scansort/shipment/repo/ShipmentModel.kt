package ie.fastway.scansort.shipment.repo

/**
 *
 */
object ShipmentModel {

    const val TABLE_NAME = "shipment"
    const val VERSION = 3

    const val COL_ID = "id"
    const val COL_TRACKING_NUMBER = "tracking_number"
    const val COL_BARCODE = "barcode"
    const val COL_RF = "regional_franchise"
    const val COL_CF = "courier_franchise"
    const val COL_ADDRESS = "address"

    /**
     * Stored as unix time (seconds since epoch).
     */
    const val COL_UPDATED_AT = "updated_at"

    /**
     * The time this particular row was entered into this SQLite DB.
     * NOT the time it was created on the back-end.
     *
     * Stored as unix time (seconds since epoch).
     */
    const val COL_ENTRY_CREATED_AT = "entry_created_at"

}

