package com.mapbox.navigation.core.internal.accounts

import com.mapbox.navigation.core.accounts.MapboxNavigationAccounts
import com.mapbox.navigation.core.accounts.TokenGeneratorProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Assert.assertEquals
import org.junit.Test

class MapboxNavigationAccountsTest {

    @Test(expected = IllegalStateException::class)
    fun obtainSkuToken_when_resourceUrl_empty() {
        val result = MapboxNavigationAccounts.obtainUrlWithSkuToken("", 4)

        assertEquals("", result)
    }

    @Test(expected = IllegalStateException::class)
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_and_querySize_lessThan_zero() {
        MapboxNavigationAccounts.obtainUrlWithSkuToken("http://www.mapbox.com", -1)
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_querySize_zero() {
        mockkObject(TokenGeneratorProvider)
        every { TokenGeneratorProvider.getNavigationTokenGenerator() } returns mockk {
            every { getSKUToken() } returns "12345"
        }

        val result = MapboxNavigationAccounts.obtainUrlWithSkuToken(
            "http://www.mapbox.com/some/params/",
            0
        )

        assertEquals("http://www.mapbox.com/some/params/?sku=12345", result)
        unmockkObject(TokenGeneratorProvider)
    }

    @Test
    fun obtainSkuToken_when_resourceUrl_notNullOrEmpty_querySize_not_zero() {
        mockkObject(TokenGeneratorProvider)
        every { TokenGeneratorProvider.getNavigationTokenGenerator() } returns mockk {
            every { getSKUToken() } returns "12345"
        }

        val result = MapboxNavigationAccounts.obtainUrlWithSkuToken(
            "http://www.mapbox.com/some/params/?query=test",
            1
        )

        assertEquals("http://www.mapbox.com/some/params/?query=test&sku=12345", result)
        unmockkObject(TokenGeneratorProvider)
    }

    @Test
    fun obtainSkuId_is_08() {
        assertEquals("08", MapboxNavigationAccounts.obtainSkuId())
    }
}
