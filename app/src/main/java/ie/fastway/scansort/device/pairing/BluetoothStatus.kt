package ie.fastway.scansort.device.pairing

/**
 * A status event of the device's Bluetooth connection.
 */
data class BluetoothStatus(

        val isTurnedOn: Boolean,


        /**
         * TRUE if the app has permissions to use Bluetooth.
         *
         * FALSE if permissions are not granted.
         *
         * NULL if the at the point of creation it was not known whether permissions were
         * granted or not.
         */
        val isPermissionsGranted: Boolean? = null
) {

    val isStatusOk get() = (isTurnedOn && (isPermissionsGranted == true))

}