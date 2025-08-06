package com.example.edifyhub.passwordReset

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

fun getPasswordResetLink(email: String, onResult: (String?, String?) -> Unit) {
    val client = OkHttpClient()
    val url = "https://us-central1-edifyhub-e0793.cloudfunctions.net/generatePasswordResetLink"
    val json = JSONObject().put("email", email).toString()
    val body = json.toRequestBody("application/json".toMediaTypeOrNull())
    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    client.newCall(request).enqueue(object: Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult(null, "Network error. Please try again.")
        }
        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body?.string()
            if (responseString != null) {
                val responseJson = JSONObject(responseString)
                val link = responseJson.optString("link", null)
                val error = responseJson.optString("error", null)
                if (response.isSuccessful && link != null) {
                    onResult(link, null)
                } else {
                    onResult(null, error ?: "Unknown error occurred.")
                }
            } else {
                onResult(null, "No response from server.")
            }
        }
    })
}