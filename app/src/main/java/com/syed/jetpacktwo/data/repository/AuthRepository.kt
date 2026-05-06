package com.syed.jetpacktwo.data.repository

import com.syed.jetpacktwo.data.local.PreferenceManager
import com.syed.jetpacktwo.data.model.LoginRequest
import com.syed.jetpacktwo.data.model.LoginResponse
import com.syed.jetpacktwo.data.remote.ApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

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
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDevices(): Result<com.syed.jetpacktwo.data.model.DeviceListResponse> {
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
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch devices"))
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
