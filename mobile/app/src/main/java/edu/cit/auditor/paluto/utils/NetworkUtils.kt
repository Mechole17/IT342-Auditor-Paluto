package edu.cit.auditor.paluto.utils

import com.google.gson.Gson
import edu.cit.auditor.paluto.api.ApiResponse
import retrofit2.Response

object NetworkUtils {
    /**
     * Parses the errorBody from a Retrofit Response into a readable message.
     * Can be used by any Activity or Fragment making API calls.
     */
    fun parseError(response: Response<*>): String {
        val rawError = response.errorBody()?.string() ?: "Unknown error"
        return try {
            val gson = Gson()
            val errorResponse = gson.fromJson(rawError, ApiResponse::class.java)
            errorResponse?.error?.message ?: "Error code: ${response.code()}"
        } catch (e: Exception) {
            // If it's not JSON (e.g. HTML error page), return the HTTP status and a snippet of the error
            val snippet = if (rawError.length > 50) rawError.take(50) + "..." else rawError
            "HTTP ${response.code()}: $snippet"
        }
    }
}