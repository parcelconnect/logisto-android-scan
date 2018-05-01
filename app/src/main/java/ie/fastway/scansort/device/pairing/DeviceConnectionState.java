package ie.fastway.scansort.device.pairing;

import android.support.annotation.IntDef;

/**
 * An event that indicates the state of a device being connected via Bluetooth.
 */
public class DeviceConnectionState {

    public static final int STATE_UNDEFINED = 0;
    public static final int STATE_IS_CONNECTING = 2;
    public static final int STATE_CONNECTED = 4;
    public static final int STATE_NOT_CONNECTED = 8;

    @IntDef({STATE_UNDEFINED, STATE_IS_CONNECTING, STATE_CONNECTED, STATE_NOT_CONNECTED})
    @interface ConnectionStatus {
    }

    private final int connectionStatus;

    private DeviceConnectionState(@ConnectionStatus int connectionState) {
        this.connectionStatus = connectionState;
    }

    //----------------------------------------------------------------------------------------------
    // FACTORY METHODS
    //----------------------------------------------------------------------------------------------

    public static DeviceConnectionState noState() {
        return new DeviceConnectionState(STATE_UNDEFINED);
    }

    public static DeviceConnectionState onDeviceConnected() {
        return new DeviceConnectionState(STATE_CONNECTED);
    }

    public static DeviceConnectionState onAttemptingToPair() {
        return new DeviceConnectionState(STATE_IS_CONNECTING);
    }

    public static DeviceConnectionState onDeviceNotConnected() {
        return new DeviceConnectionState(STATE_NOT_CONNECTED);
    }

    //----------------------------------------------------------------------------------------------

    @ConnectionStatus
    public int getConnectionStatus() {
        return connectionStatus;
    }

    public boolean isConnected() {
        return connectionStatus == STATE_CONNECTED;
    }

    public boolean isDisconnected() {
        // This method appears to be unnecessary, but without it all events must either be connection
        // or disconnecte events, which might not be the case in the future.
        return !isConnected();
    }

    public boolean isConnecting() {
        return connectionStatus == STATE_IS_CONNECTING;
    }

    @Override
    public String toString() {
        return "DeviceConnectionState{" +
                "connectionStatus=" + connectionStatus +
                '}';
    }
}
