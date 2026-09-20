package com.technowave.trolley_robo.data.repository

import com.technowave.trolley_robo.data.local.PreferenceManager
import com.technowave.trolley_robo.data.remote.ApiService
import com.technowave.trolley_robo.data.remote.model.RackStatusDto
import com.technowave.trolley_robo.data.remote.model.StockStatusDto
import com.technowave.trolley_robo.domain.repository.ReportRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.technowave.trolley_robo.util.getErrorMessage

class ReportRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : ReportRepository {

    override suspend fun getRackStatus(custId: String): Result<List<RackStatusDto>> {
        return try {
            val baseUrl = preferenceManager.baseUrl.first()
            val fullUrl = if (baseUrl.endsWith("/")) {
                "${baseUrl}StockTake/GetRacksStatus"
            } else {
                "${baseUrl}/StockTake/GetRacksStatus"
            }
            val response = apiService.getRackStatus(fullUrl, custId)
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val mapped = dtos.mapIndexed { index, item ->
                    RackStatusDto(
                        no = index + 1,
                        department = item.department,
                        description = item.shelfName,
                        isStatusOk = item.status == 1
                    )
                }
                Result.success(mapped)
            } else {
                Result.failure(Exception(response.getErrorMessage()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStockStatus(custId: String): Result<List<StockStatusDto>> {
        return try {
            val baseUrl = preferenceManager.baseUrl.first()
            val fullUrl = if (baseUrl.endsWith("/")) {
                "${baseUrl}StockTake/GetDepartmentVariance"
            } else {
                "${baseUrl}/StockTake/GetDepartmentVariance"
            }
            val response = apiService.getStockStatus(fullUrl, custId)
            if (response.isSuccessful) {
                val list = response.body() ?: emptyList()
                list.forEachIndexed { index, item -> item.no = index + 1 }
                Result.success(list)
            } else {
                Result.failure(Exception(response.getErrorMessage()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
