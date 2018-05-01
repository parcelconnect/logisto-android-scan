package com.tubbert.powdroid.mocking

import java.util.*

/**
 * Used for creating fake data.
 */
open class Fakist {

    val rand = Random()

    fun createNumericString(length: Int): String {
        val sb = StringBuilder()
        for (i in 1..10) {
            sb.append(rand.nextInt(10))
        }
        return sb.toString()
    }

    fun makeFakeString(length: Int): String {
        var uuid = UUID.randomUUID().toString()
        uuid = uuid.replace("-", "")

        return if (uuid.length > length) {
            uuid.substring(0, length)
        }
        else {
            uuid + makeFakeString(length - uuid.length)
        }
    }

    fun makeHexValue() = Integer.toHexString(rand.nextInt(16))


    fun makeBluetoothAddress(): String {
        return makeHexValue() + ":" + makeHexValue() + ":" + makeHexValue() + ":" + makeHexValue()
    }

    fun nextId(): Long {
        return rand.nextLong()
    }

    fun nextAddress(): String {
        val houseNumber = rand.nextInt(999)
        return  "$houseNumber Fake Street, Fakist City, Eibhir, Ireland"
    }

    fun nextTrackingNumber() = makeFakeString(10)

    fun chooseFrom(vararg choices: String): String {
        return choices[rand.nextInt(choices.size)]
    }

}