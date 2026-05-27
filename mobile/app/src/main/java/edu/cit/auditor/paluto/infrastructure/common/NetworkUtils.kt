package edu.cit.auditor.paluto.infrastructure.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import edu.cit.auditor.paluto.infrastructure.api.ApiResponse
import retrofit2.Response

object NetworkUtils {
    /**
     * Checks if the device has an active internet connection.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

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