package ie.fastway.scansort.shipment.repo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ie.fastway.scansort.logging.LogConfig
import org.jetbrains.anko.db.*
import timber.log.Timber


/**
 * Access property for Context:
 */
val Context.shipmentDb: ShipmentDbOpenHelper
    get() = ShipmentDbOpenHelper.getInstance(applicationContext)

/**
 *
 */
class ShipmentDbOpenHelper(ctx: Context)
    : ManagedSQLiteOpenHelper(ctx, ShipmentModel.TABLE_NAME, null, ShipmentModel.VERSION) {

    companion object {
        private var instance: ShipmentDbOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): ShipmentDbOpenHelper {
            if (instance == null) {
                instance = ShipmentDbOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }

        /**
         * Must be called when tearing down tests.
         */
        @Synchronized
        internal fun clearSingleton() {
            instance = null
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        if (LogConfig.SHIPMENT_DB) {
            Timber.d("Creating Shipment table...")
        }

        db.createTable(
                ShipmentModel.TABLE_NAME,
                true, // :ifNotExists
                ShipmentModel.COL_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                ShipmentModel.COL_TRACKING_NUMBER to TEXT + UNIQUE,
                ShipmentModel.COL_BARCODE to TEXT + UNIQUE,
                ShipmentModel.COL_CF to TEXT,
                ShipmentModel.COL_RF to TEXT,
                ShipmentModel.COL_ADDRESS to TEXT,
                ShipmentModel.COL_UPDATED_AT to INTEGER,
                ShipmentModel.COL_ENTRY_CREATED_AT to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (LogConfig.SHIPMENT_DB) {
            Timber.d("ShipmentDB: onUpgrade from v$oldVersion to v$newVersion.")
        }

        // We don't have a migration path, so just drop and re-create the database.
        dropAllTables(db)
        onCreate(db)
    }

    fun clearAndRecreateDb() {
        clearAllTables()
        use {
            onCreate(this)
        }
    }

    fun clearAllTables() {
        use {
            dropAllTables(this)
        }
    }

    private fun dropAllTables(db: SQLiteDatabase) {
        db.dropTable(ShipmentModel.TABLE_NAME, ifExists = true)
    }

}