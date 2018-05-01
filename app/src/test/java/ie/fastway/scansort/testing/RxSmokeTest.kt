package ie.fastway.scansort.testing

import io.reactivex.Flowable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test

/**
 * Ensures that RxJava has been set up correctly.
 *
 */
class RxSmokeTest {

    companion object {
        const val HELLO_WORLD = "Hello, world!"
    }

    lateinit var lastMessage1: String
    lateinit var lastMessage2: String

    @Before
    fun setUp() {
        lastMessage1 = ""
        lastMessage2 = ""
    }

    /**
     * Register two observables and execute a sample scannedValue.
     */
    @Test
    @Throws(Exception::class)
    fun testSayHello() {
        sayHello()

        assertThat(lastMessage1, `is`(HELLO_WORLD))
        assertThat(lastMessage2, `is`(HELLO_WORLD))
    }

    fun observeMessage1(message: String) {
        lastMessage1 = message
    }

    fun observeMessage2(message: String) {
        lastMessage2 = message
    }

    fun sayHello() {
        var flowable = Flowable.just(HELLO_WORLD)
        flowable.subscribe(this::observeMessage1)
        flowable.subscribe(this::observeMessage2)
    }

}