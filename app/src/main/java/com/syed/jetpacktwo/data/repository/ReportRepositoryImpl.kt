package com.syed.jetpacktwo.data.repository

import com.syed.jetpacktwo.data.remote.ApiService
import com.syed.jetpacktwo.data.remote.model.RackStatusDto
import com.syed.jetpacktwo.data.remote.model.StockStatusDto
import com.syed.jetpacktwo.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ReportRepository {

    override suspend fun getRackStatus(): Result<List<RackStatusDto>> {
        return try {
            val response = apiService.getRackStatus()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch rack status: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStockStatus(): Result<List<StockStatusDto>> {
        return try {
            val response = apiService.getStockStatus()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch stock status: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
