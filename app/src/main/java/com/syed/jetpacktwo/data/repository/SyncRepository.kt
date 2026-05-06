package com.syed.jetpacktwo.data.repository

import com.syed.jetpacktwo.data.local.PreferenceManager
import com.syed.jetpacktwo.data.local.db.ScannedTagDao
import com.syed.jetpacktwo.data.model.StockTakeInfo
import com.syed.jetpacktwo.data.model.StockTakeRequest
import com.syed.jetpacktwo.data.model.StockTakeResponse
import com.syed.jetpacktwo.data.remote.ApiService
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val scannedTagDao: ScannedTagDao,
    private val preferenceManager: PreferenceManager
) {
    suspend fun uploadInventory(): Result<StockTakeResponse> {
        return try {
            val tags = scannedTagDao.getAllTags().first()
            if (tags.isEmpty()) {
                return Result.failure(Exception("No tags to upload"))
            }

            val baseUrl = preferenceManager.baseUrl.first()
            val deviceId = preferenceManager.deviceId.first().ifEmpty { "1" }
            
            val fullUrl = if (baseUrl.endsWith("/")) {
                "${baseUrl}StockTake/StockTakeSave"
            } else {
                "${baseUrl}/StockTake/StockTakeSave"
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            // Group tags by their stored schedulerId
            val groupedTags = tags.groupBy { it.schedulerId }
            var overallSuccess = true
            var errorDescription = ""

            groupedTags.forEach { (sId, tagList) ->
                val stockTakeInfoList = tagList.map { tag ->
                    StockTakeInfo(
                        rfid = tag.epc,
                        scanDate = dateFormat.format(Date(tag.timestamp)),
                        location = 0
                    )
                }

                val request = StockTakeRequest(
                    custID = "00",
                    schedulerID = sId,
                    deviceID = deviceId,
                    stockTakeInfo = stockTakeInfoList
                )

                val response = apiService.uploadStockTake(fullUrl, request)
                if (response.isSuccessful && response.body()?.status == true) {
                    // Clear only the uploaded tags for this scheduler
                    scannedTagDao.deleteTags(tagList)
                } else {
                    overallSuccess = false
                    errorDescription = response.body()?.errorDescription ?: response.errorBody()?.string() ?: "Upload failed for scheduler $sId"
                }
            }

            if (overallSuccess) {
                Result.success(StockTakeResponse(0, true, "Success"))
            } else {
                Result.failure(Exception(errorDescription))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
