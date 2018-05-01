package ie.fastway.scansort.device

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import com.tubbert.powdroid.mocking.Fakist
import ie.fastway.scansort.device.pairing.DeviceConnectionState
import ie.fastway.scansort.device.scanner.ScanningDevice
import java.util.*


/**
 * Represents a device that can be paired via Bluetooth.
 * Wraps BluetoothDevice to make it easier to work with mock devices.
 *
 */
class ConnectableDevice
private constructor(
        private val bluetoothDevice: BluetoothDevice? = null,
        private val mockAddress: String? = null,
        val mockName: String? = null,
        var deviceState: DeviceConnectionState,
        var connectedScanner: ScanningDevice? = null,
        val mockBluetoothClassInt: Int = BluetoothProfile.PERIPHERAL_NON_KEYBOARD_NON_POINTING
) {

    /**
     * Constants which can be found in the source code for [BluetoothClass.Device],
     * but they are marked with the `@hide` annotation, so they cannot
     * be refered to directly, thus we copy them here.
     *
     * Of course, this makes our implementation fragile, as we can assume these constants
     * were hidden for a reason. However, these constants are our only way of determining whether
     * a device is a keyboard scanner or not, so it can't be avoided.
     */
    object BluetoothProfile {

        const val PERIPHERAL_NON_KEYBOARD_NON_POINTING = 0x0500;

        const val  PERIPHERAL_KEYBOARD = 0x0540

        const val PERIPHERAL_POINTING = 0x0580

        const val PERIPHERAL_KEYBOARD_POINTING = 0x05C0
    }

    /**
     * A common name used to describe the scanning device.
     *
     */
    val productName: String? = null

    /**
     * Returns the hardware address of this BluetoothDevice.
     * <p> For example, "00:11:22:AA:BB:CC".
     * @return Bluetooth hardware address as string
     */
    fun getBluetoothAddress(): String {
        bluetoothDevice?.let {
            return it.address
        }
        mockAddress?.let {
            return it
        }

        throw IllegalStateException("No Bluetooth address available, neither BluetoothDevice nor mockAddress are set")
    }

    fun getBluetoothName(): String {
        bluetoothDevice?.let {
            return if (it.name != null) {
                it.name
            }
            else {
                // Name not available, so we return no name.
                ""
            }
        }

        mockName?.let { return it }

        throw IllegalStateException("No Bluetooth name available, neither BluetoothDevice nor mockName is set.")
    }

    fun getBondState(): DeviceConnectionState {
        return deviceState
    }

    fun attemptToBond() {
        bluetoothDevice?.createBond()
    }

    /**
     * Gets the integer defining the [BluetoothClass] of this device.
     */
    fun getBluetoothClass(): Int {
        val realBluetoothDevice = bluetoothDevice

        return if(realBluetoothDevice == null) {
            mockBluetoothClassInt
        }
        else {
            val bluetoothClass = realBluetoothDevice.bluetoothClass

            // This is the only way of getting the raw int of the class without
            // writing to a Parcel.
            bluetoothClass.hashCode()
        }

        /*
        // Create an empty Bluetooth Class.
        val parcel = Parcel.obtain()
        parcel.writeInt(mockBluetoothClassInt)
        val bluetoothClass = BluetoothClass.CREATOR.createFromParcel(parcel)
        parcel.recycle()
        return bluetoothClass
        */
    }

    /*
    fun breakBond() {
        android.bluetooth.BluetoothDevice#removeBond()
        bluetoothDevice?.removeBond()
    }
    */

    /**
     * Gets a unique name that identifies this device amongst all others.
     */
    fun getDeviceId() = getBluetoothName()

    override fun toString(): String {
        return "ConnectableDevice(bluetoothDevice=$bluetoothDevice, " +
                "address=${getBluetoothAddress()}, " +
                "name=${getBluetoothName()}, " +
                "deviceState=$deviceState, " +
                "productName=$productName" +
                ")"
    }

    // region equals() & hashCode() ----

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConnectableDevice

        return (getBluetoothAddress() == other.getBluetoothAddress())
    }

    override fun hashCode(): Int {
        return getBluetoothAddress().hashCode()
    }

    // endregion

    //----------------------------------------------------------------------------------------------

    object Factory {

        private val fakist = Fakist()

        fun createFromBluetoothDevice(
                bluetoothDevice: BluetoothDevice
        ): ConnectableDevice {

            val deviceState = when (bluetoothDevice.bondState) {

                BluetoothDevice.BOND_BONDED ->
                    DeviceConnectionState.onDeviceConnected()

                BluetoothDevice.BOND_BONDING ->
                    DeviceConnectionState.noState()

                BluetoothDevice.BOND_NONE ->
                    DeviceConnectionState.onDeviceNotConnected()

                else ->
                    DeviceConnectionState.noState()
            }

            return ConnectableDevice(
                    bluetoothDevice = bluetoothDevice,
                    deviceState = deviceState
            )
        }

        fun createMockPathfinder(
                deviceState: DeviceConnectionState = DeviceConnectionState.noState()
        ): ConnectableDevice {
            val deviceId = fakist.makeFakeString(8).toUpperCase(Locale.UK)

            return ConnectableDevice(
                    mockName = "6140 $deviceId",
                    mockAddress = fakist.makeFakeString(10),
                    deviceState = deviceState
            )
        }

        fun createMockSymbolScanner(): ConnectableDevice {
            return createMock(
                    name = "CS4070:" + fakist.createNumericString(14),
                    address = fakist.makeBluetoothAddress()
            )
        }

        fun createMock(
                name: String = fakist.makeFakeString(10),
                address: String = fakist.makeBluetoothAddress(),
                deviceState: DeviceConnectionState = DeviceConnectionState.noState()
        ): ConnectableDevice {

            return ConnectableDevice(
                    mockName = name,
                    mockAddress = address,
                    deviceState = deviceState
            )

        }
    }

}