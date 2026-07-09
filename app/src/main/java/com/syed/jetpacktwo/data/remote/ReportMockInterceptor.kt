package com.syed.jetpacktwo.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportMockInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.encodedPath

        val (responseString, code) = when (url) {
            "/api/reports/rack-status" -> getMockRackStatus() to 200
            "/api/reports/stock-status" -> getMockStockStatus() to 200
            else -> return chain.proceed(request) // Pass through other requests
        }

        // Simulate network delay
        Thread.sleep(1000)

        return Response.Builder()
            .code(code)
            .message(if (code == 200) "OK" else "Error")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(responseString.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun getMockRackStatus(): String {
        return """
            [
                {"no": 1, "department": "Mens", "description": "Winter Collection sorted and scanned.", "isStatusOk": true},
                {"no": 2, "department": "Womens", "description": "Missing items in aisle B.", "isStatusOk": false},
                {"no": 3, "department": "Kids", "description": "Fully restocked and synced.", "isStatusOk": true},
                {"no": 4, "department": "Accessories", "description": "Inventory count mismatch.", "isStatusOk": false},
                {"no": 5, "department": "Footwear", "description": "Pending manager review.", "isStatusOk": false},
                {"no": 6, "department": "Electronics", "description": "All high-value items secure.", "isStatusOk": true},
                {"no": 7, "department": "Home & Living", "description": "Shelving re-arranged.", "isStatusOk": true}
            ]
        """.trimIndent()
    }

    private fun getMockStockStatus(): String {
        return """
            [
                {"no": 1, "department": "Mens", "expected": 150, "scanned": 150},
                {"no": 2, "department": "Womens", "expected": 200, "scanned": 195},
                {"no": 3, "department": "Kids", "expected": 120, "scanned": 120},
                {"no": 4, "department": "Accessories", "expected": 300, "scanned": 290},
                {"no": 5, "department": "Footwear", "expected": 80, "scanned": 85},
                {"no": 6, "department": "Electronics", "expected": 50, "scanned": 50},
                {"no": 7, "department": "Home & Living", "expected": 100, "scanned": 98}
            ]
        """.trimIndent()
    }
}
