package com.example.edifyhub

import android.app.Application
import com.cloudinary.android.MediaManager

class EdifyHubApp : Application() {
    companion object {
        private var cloudinaryInitialized = false
    }

    override fun onCreate() {
        super.onCreate()
        if (!cloudinaryInitialized) {
            val config: HashMap<String, String> = HashMap()
            config["cloud_name"] = "dnbijfphs"
            config["api_key"] = "868239199586873"
            config["api_secret"] = "qlEJucb-nwWuZ7ykH8qmi22gGOM"
            MediaManager.init(this, config)
            cloudinaryInitialized = true
        }
    }
}