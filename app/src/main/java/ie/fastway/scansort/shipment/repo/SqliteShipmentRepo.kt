package ie.fastway.scansort.shipment.repo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ie.fastway.scansort.logging.LogConfig
import ie.fastway.scansort.shipment.Shipment
import ie.logistio.equinox.Equinox
import org.jetbrains.anko.db.*
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber


/**
 * Repo for storing Shipment data in an on-device SQLite database.
 */
class SqliteShipmentRepo(context: Context) : ShipmentRepo {

    private val dbOpenHelper = context.shipmentDb
    private val shipmentParser = ShipmentRowParser()
    private var progressCallback: StorageProgressWatcher? = null

    override fun storeAll(shipmentsToStore: Collection<Shipment>) {
        val totalShipmentCount = shipmentsToStore.size
        dbOpenHelper.use {
            transaction {
                var i = 1
                for (shipment in shipmentsToStore) {
                    progressCallback?.invoke(i, totalShipmentCount)
                    storeShipmentInDb(shipment, this)
                    i++
                }
            }
        }

        progressCallback?.invoke(totalShipmentCount, totalShipmentCount)
    }

    private fun storeShipmentInDb(shipment: Shipment, db: SQLiteDatabase) {
        db.replace(ShipmentModel.TABLE_NAME,
                ShipmentModel.COL_ID to shipment.id,
                ShipmentModel.COL_TRACKING_NUMBER to shipment.trackingNumber,
                ShipmentModel.COL_BARCODE to shipment.barcode,
                ShipmentModel.COL_CF to shipment.courierFranchisee,
                ShipmentModel.COL_RF to shipment.regionalFranchisee,
                ShipmentModel.COL_ADDRESS to shipment.address,
                ShipmentModel.COL_UPDATED_AT to convertToDbTimestamp(shipment.updatedAt),
                ShipmentModel.COL_ENTRY_CREATED_AT to Equinox.nowUnixTimestamp()
        )
    }

    override fun findShipmentByBarcodeAsync(
            barcode: String, callback: (barcode: String, foundShipment: Shipment?) -> Unit) {

        if (LogConfig.SHIPMENTS) {
            Timber.d("Finding shipment by tracking number:$barcode")
        }

        useDb {
            with(ShipmentModel) {
                selectAllColumns().whereArgs(
                        "$COL_BARCODE = {barcode}",
                        "barcode" to barcode
                ).exec {

                    if (LogConfig.SHIPMENTS) {
                        Timber.d("Shipment cursor result count=" + count)
                    }

                    callback(barcode, parseList(shipmentParser).getOrNull(0))
                }
            }
        }
    }

    override fun findLatestShipment(callback: (Shipment?) -> Unit) {

        if (LogConfig.SHIPMENTS) {
            Timber.d("Finding last known shipment...")
        }

        useDb {
            with(ShipmentModel) {
                selectAllColumns()
                        .limit(1)
                        .orderBy(COL_UPDATED_AT, SqlOrderDirection.DESC)
                        .exec {
                            callback(parseOpt(shipmentParser))
                        }
            }
        }

    }

    //----------------------------------------------------------------------------------------------
    // DELETE OLD DATA
    //----------------------------------------------------------------------------------------------

    /**
     * Deletes data from the repo that can be safely assumed to be so old that
     * it no longer needs to be stored on the device.
     */
    fun purgeExpiredData() {
        useDb {
            with(ShipmentModel) {
                val threshold = Equinox.now().minus(7, ChronoUnit.DAYS).epochSecond
                val deleteCount = delete(TABLE_NAME,
                        "$COL_UPDATED_AT < {threshold}",
                        "threshold" to threshold
                )

                if (LogConfig.SHIPMENT_DB) {
                    Timber.d("purgeExpiredData; deletedShipmentCount=$deleteCount;")
                }

            }
        }

    }

    //----------------------------------------------------------------------------------------------

    private fun SQLiteDatabase.selectAllColumns(): SelectQueryBuilder =
            with(ShipmentModel) {
                select(TABLE_NAME,
                        COL_ID,
                        COL_TRACKING_NUMBER,
                        COL_BARCODE,
                        COL_CF,
                        COL_RF,
                        COL_ADDRESS,
                        COL_UPDATED_AT,
                        COL_ENTRY_CREATED_AT
                )
            }


    private fun convertToDbTimestamp(instant: Instant): Long {
        return instant.epochSecond
    }

    override fun setStorageProgressCallback(watcher: StorageProgressWatcher) {
        this.progressCallback = watcher
    }

    private fun <T> useDb(f: SQLiteDatabase.() -> T): T = dbOpenHelper.use(f)

    class ShipmentRowParser : MapRowParser<Shipment> {

        override fun parseRow(columns: Map<String, Any?>): Shipment {
            if (LogConfig.SHIPMENT_DB) {
                Timber.d("Parsing Shipment row; columns=$columns;")
            }

            with(ShipmentModel) {

                val updatedAt = Equinox.createFromUnixTimestamp(
                        columns[COL_UPDATED_AT] as Long)

                return Shipment(
                        id = columns[COL_ID] as Long,
                        trackingNumber = columns[COL_TRACKING_NUMBER] as String,
                        barcode = columns[COL_BARCODE] as String,
                        courierFranchisee = columns[COL_CF] as String?,
                        regionalFranchisee = columns[COL_RF] as String?,
                        address = columns[COL_ADDRESS] as String?,
                        updatedAt = updatedAt
                )
            }

        }

    }

}