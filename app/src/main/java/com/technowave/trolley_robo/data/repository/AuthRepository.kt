package com.technowave.trolley_robo.data.repository

import com.technowave.trolley_robo.data.local.PreferenceManager
import com.technowave.trolley_robo.data.model.LoginRequest
import com.technowave.trolley_robo.data.model.LoginResponse
import com.technowave.trolley_robo.data.remote.ApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import com.technowave.trolley_robo.util.getErrorMessage

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val baseUrl = preferenceManager.baseUrl.first()
            val fullUrl = if (baseUrl.endsWith("/")) {
                "${baseUrl}Users/UserAuthenticationDevice"
            } else {
                "${baseUrl}/Users/UserAuthenticationDevice"
            }
            
            val response = apiService.login(fullUrl, request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // Store scheduler ID for later use in upload
                body.maxSchedulerID?.let {
                    preferenceManager.saveSchedulerId(it.toString())
                }
                Result.success(body)
            } else {
                Result.failure(Exception(response.getErrorMessage()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDevices(): Result<com.technowave.trolley_robo.data.model.DeviceListResponse> {
        return try {
            val baseUrl = preferenceManager.baseUrl.first()
            val fullUrl = if (baseUrl.endsWith("/")) {
                "${baseUrl}Device/GetDeviceList"
            } else {
                "${baseUrl}/Device/GetDeviceList"
            }
            val response = apiService.getDeviceList(fullUrl)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.getErrorMessage()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveDevice(id: String, name: String) {
        preferenceManager.saveDevice(id, name)
    }

    fun getDeviceId() = preferenceManager.deviceId
    fun getDeviceName() = preferenceManager.deviceName

    suspend fun saveBaseUrl(url: String) {
        preferenceManager.saveBaseUrl(url)
    }

    fun getBaseUrl() = preferenceManager.baseUrl

    suspend fun saveEpcFilter(filter: String) {
        preferenceManager.saveEpcFilter(filter)
    }

    fun getEpcFilter() = preferenceManager.epcFilter
}
