package com.syed.jetpacktwo.domain.repository

import com.syed.jetpacktwo.data.remote.model.RackStatusDto
import com.syed.jetpacktwo.data.remote.model.StockStatusDto

interface ReportRepository {
    suspend fun getRackStatus(): Result<List<RackStatusDto>>
    suspend fun getStockStatus(): Result<List<StockStatusDto>>
}
