package com.technowave.trolley_robo.domain.repository

import com.technowave.trolley_robo.data.remote.model.RackStatusDto
import com.technowave.trolley_robo.data.remote.model.StockStatusDto

interface ReportRepository {
    suspend fun getRackStatus(custId: String): Result<List<RackStatusDto>>
    suspend fun getStockStatus(custId: String): Result<List<StockStatusDto>>
}
