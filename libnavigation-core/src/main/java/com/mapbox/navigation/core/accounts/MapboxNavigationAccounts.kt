package com.mapbox.navigation.core.accounts

import com.mapbox.navigation.base.internal.accounts.UrlSkuTokenProvider

/**
 * This class generates and retains the Navigation SDK's SKU token according to internal Mapbox policies
 */
internal object MapboxNavigationAccounts : UrlSkuTokenProvider {

    private const val SKU_KEY = "sku"

    /**
     * Returns a token attached to the URL query or the given [resourceUrl].
     */
    @Synchronized
    override fun obtainUrlWithSkuToken(resourceUrl: String, querySize: Int): String {
        return when {
            querySize < 0 -> throw IllegalStateException("querySize cannot be less than 0")
            resourceUrl.isEmpty() -> throw IllegalStateException("resourceUrl cannot be empty")
            else -> {
                buildResourceUrlWithSku(
                    resourceUrl,
                    querySize,
                    TokenGeneratorProvider.getNavigationTokenGenerator().getSKUToken()
                )
            }
        }
    }

    // fixme workaround for missing the public SKU ID constant
    internal fun obtainSkuId(): String = "08"

    private fun buildResourceUrlWithSku(
        resourceUrl: String,
        querySize: Int,
        skuToken: String
    ): String {
        val urlBuilder = StringBuilder(resourceUrl)
        when (querySize == 0) {
            true -> urlBuilder.append("?")
            false -> urlBuilder.append("&")
        }
        urlBuilder.append("$SKU_KEY=$skuToken")
        return urlBuilder.toString()
    }
}
