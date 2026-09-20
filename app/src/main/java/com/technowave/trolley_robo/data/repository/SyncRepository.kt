package com.technowave.trolley_robo.data.repository

import com.technowave.trolley_robo.data.local.PreferenceManager
import com.technowave.trolley_robo.data.local.db.ScannedTagDao
import com.technowave.trolley_robo.data.model.StockTakeInfo
import com.technowave.trolley_robo.data.model.StockTakeRequest
import com.technowave.trolley_robo.data.model.StockTakeResponse
import com.technowave.trolley_robo.data.remote.ApiService
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import com.technowave.trolley_robo.util.getErrorMessage

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val scannedTagDao: ScannedTagDao,
    private val preferenceManager: PreferenceManager
) {
    suspend fun uploadInventory(): Result<Pair<StockTakeResponse, List<com.technowave.trolley_robo.data.local.db.ScannedTag>>> {
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
            val successfullyUploaded = mutableListOf<com.technowave.trolley_robo.data.local.db.ScannedTag>()

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
                    successfullyUploaded.addAll(tagList)
                } else {
                    overallSuccess = false
                    errorDescription = response.body()?.errorDescription ?: response.getErrorMessage()
                }
            }

            if (overallSuccess) {
                Result.success(Pair(StockTakeResponse(0, true, "Success"), successfullyUploaded))
            } else {
                Result.failure(Exception(errorDescription))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
