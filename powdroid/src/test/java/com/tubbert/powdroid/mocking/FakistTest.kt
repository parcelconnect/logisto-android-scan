package com.tubbert.powdroid.mocking

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 *
 */
class FakistTest {

    lateinit var fakist: Fakist

    @Before
    fun setUp() {
        fakist = Fakist()
    }

    @Test
    fun testChooseFromArray() {
        val choiceList = arrayOf("ICE", "COLD", "JELLY")
        val chosenResults = HashSet<String>()
        for (i in 1..100) {
            val chosenValue = fakist.chooseFrom(*choiceList)
            assertTrue(choiceList.contains(chosenValue))
            chosenResults.add(chosenValue)
        }

        assertEquals(choiceList.size, chosenResults.size)
    }

    @Test
    fun testMakeFakeString() {

        for (i in arrayOf(1000, 477, 4728)) {
            assertEquals(i, fakist.makeFakeString(i).length)
        }

    }
}