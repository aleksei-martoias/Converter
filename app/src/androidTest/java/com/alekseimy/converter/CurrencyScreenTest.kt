package com.alekseimy.converter

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.alekseimy.converter.main.MainActivity
import okhttp3.mockwebserver.MockWebServer
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class CurrencyScreenTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.alekseimy.converter", appContext.packageName)
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun itemsReordersAfterClock() {
    }
}
