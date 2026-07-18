package com.syed.jetpacktwo.domain.repository

import com.syed.jetpacktwo.data.remote.model.RackStatusDto
import com.syed.jetpacktwo.data.remote.model.StockStatusDto

interface ReportRepository {
    suspend fun getRackStatus(custId: String): Result<List<RackStatusDto>>
    suspend fun getStockStatus(custId: String): Result<List<StockStatusDto>>
}
