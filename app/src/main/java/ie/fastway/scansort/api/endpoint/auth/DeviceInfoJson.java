package ie.fastway.scansort.api.endpoint.auth;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.tubbert.powdroid.util.AndroidDevice;

import static ie.fastway.scansort.api.FastwayApi.Const.ANDROID_SDK;
import static ie.fastway.scansort.api.FastwayApi.Const.APP_VERSION_INT;
import static ie.fastway.scansort.api.FastwayApi.Const.APP_VERSION_NAME;
import static ie.fastway.scansort.api.FastwayApi.Const.IMEI;
import static ie.fastway.scansort.api.FastwayApi.Const.SIM_SERIAL;

/**
 *
 */
public class DeviceInfoJson {

    @SerializedName(APP_VERSION_INT)
    public String appVersionInt;

    @SerializedName(APP_VERSION_NAME)
    public String appVersionName;

    @SerializedName(IMEI)
    public String imei;

    @SerializedName(SIM_SERIAL)
    public String simSerial;

    @SerializedName(ANDROID_SDK)
    public String androidSdkInt;

    public static DeviceInfoJson createForUnknown() {
        DeviceInfoJson json = new DeviceInfoJson();

        json.appVersionInt = "0";
        json.appVersionName = "0.0.0";
        json.imei = "UNKNOWN";
        json.simSerial = "UNKNOWN";
        json.androidSdkInt = String.valueOf(Build.VERSION.SDK_INT);

        return json;
    }

    public static DeviceInfoJson createDummyDeviceInfo() {
        DeviceInfoJson json = new DeviceInfoJson();

        json.appVersionInt = "0";
        json.appVersionName = "0.0.1-rc0";
        json.imei = "MOCK_IMEI";
        json.simSerial = "MOCK_SIM_SERIAL";
        json.androidSdkInt = String.valueOf(Build.VERSION.SDK_INT);

        return json;
    }

    public static DeviceInfoJson create(AndroidDevice androidDevice) {
        DeviceInfoJson json = new DeviceInfoJson();

        json.imei = androidDevice.getImei();
        json.simSerial = androidDevice.getSimSerial();
        json.androidSdkInt = String.valueOf(androidDevice.getSdkInt());

        return json;
    }

}
