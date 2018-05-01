package com.tubbert.powdroid.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager

/**
 * Captures information about the android device the app is running on.
 */
data class AndroidDevice(
        val modelName: String?,
        val imei: String?,
        val deviceSerialCode: String?,
        val simSerial: String?,
        val primaryPhoneNumber: String?,
        val sdkInt: Int
) {


    /**
     * Finds information about the current Android device.
     */
    public class Investigator(private val activity: Activity) {

        companion object {
            const val PERMISSION_REQUEST = 5823
            const val PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE

            const val FLAG_NOT_GRANTED = "FLAG_NOT_GRANTED"
            const val FLAG_UNKNOWN = "UNKNOWN"
        }

        fun ensurePermissionsAreAvailable() {
            if (!arePermissionsAvailable()) {

                ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(PHONE_PERMISSION),
                        PERMISSION_REQUEST
                )

            }
        }

        private fun arePermissionsAvailable() =
                (ContextCompat.checkSelfPermission(activity, PHONE_PERMISSION)
                        == PackageManager.PERMISSION_GRANTED)

        fun getDeviceInfo(): AndroidDevice {
            return if (arePermissionsAvailable()) {
                getDeviceWithPermissionsGranted()
            }
            else {
                AndroidDevice(
                        modelName = getDeviceModelName(),
                        imei = FLAG_NOT_GRANTED,
                        deviceSerialCode = FLAG_NOT_GRANTED,
                        simSerial = FLAG_NOT_GRANTED,
                        primaryPhoneNumber = FLAG_NOT_GRANTED,
                        sdkInt = Build.VERSION.SDK_INT
                );
            }

        }

        @SuppressLint("MissingPermission", "HardwareIds")
        private fun getDeviceWithPermissionsGranted(): AndroidDevice {
            val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val phoneNumber = telephonyManager.line1Number

            val simSerial = telephonyManager.simSerialNumber

            val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            }
            else {
                telephonyManager.deviceId
            }

            val deviceSerial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            }
            else {
                Build.SERIAL
            }

            return AndroidDevice(
                    modelName = getDeviceModelName(),
                    imei = imei,
                    deviceSerialCode = deviceSerial,
                    simSerial = simSerial,
                    primaryPhoneNumber = phoneNumber,
                    sdkInt = Build.VERSION.SDK_INT
            )
        }

        private fun getDeviceModelName() = Build.MODEL

        /**
         * Gets the name of the version of the Application.
         */
        fun getAppVersionName(): String {
            val packageInfo: PackageInfo
            try {
                packageInfo = activity.packageManager
                        .getPackageInfo(activity.packageName, 0)

                return packageInfo.versionName
            }
            catch (e: PackageManager.NameNotFoundException) {
                return "name_not_found"
            }

        }

        /**
         * Gets the version-int of the Application.
         */
        fun getAppVersionInt(): Int {
            val packageInfo: PackageInfo

            try {
                packageInfo = activity.packageManager
                        .getPackageInfo(activity.packageName, 0)

                return packageInfo.versionCode
            }
            catch (e: PackageManager.NameNotFoundException) {
                return 0
            }

        }


    }


}