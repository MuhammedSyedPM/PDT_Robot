package com.syed.jetpacktwo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Hilt generates the dependency container here.
 * All RFID device logic lives in the data layer (RfidRepositoryImpl), not in this class.
 */
@HiltAndroidApp
class JetPackTwoApp : Application()
