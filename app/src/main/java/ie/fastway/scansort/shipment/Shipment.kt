package ie.fastway.scansort.shipment

import org.threeten.bp.Instant

/**
 *
 */
data class Shipment(
        val id: Long,
        val trackingNumber: String,
        val barcode: String,
        val courierFranchisee: String?,
        val regionalFranchisee: String?,
        val address: String?,
        val updatedAt: Instant
)