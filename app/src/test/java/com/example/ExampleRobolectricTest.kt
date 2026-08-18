package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Expense Tracker", appName)
    }

    @Test
    fun `test currency formatting default symbol`() {
        val formatted = CurrencyUtils.format(250.0)
        assertEquals("৳250.00", formatted)

        val compact = CurrencyUtils.formatCompact(500.0)
        assertEquals("৳500", compact)
    }
}
