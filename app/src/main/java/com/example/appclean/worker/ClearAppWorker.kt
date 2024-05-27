package com.example.appclean.worker


import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ClearAppDataWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val packageName = inputData.getString("PACKAGE_NAME") ?: return Result.failure()
        Log.d("ClearAppDataWorker", "Attempting to clear data for $packageName")

        return try {
            clearAppDataUsingADB(packageName)
            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ClearAppDataWorker", "Failed to clear data for $packageName", e)
            Result.failure()
        }
    }

    private fun clearAppDataUsingADB(packageName: String) {
        try {
            val process = Runtime.getRuntime().exec("adb shell pm clear $packageName")
            val result = process.waitFor()
            if (result == 0) {
                Log.d("ClearAppDataWorker", "Successfully cleared data for $packageName")
            } else {
                Log.e("ClearAppDataWorker", "Failed to clear data for $packageName, exit code: $result")
            }
        } catch (e: Exception) {
            Log.e("ClearAppDataWorker", "Error clearing data for $packageName using ADB", e)
        }
    }
}