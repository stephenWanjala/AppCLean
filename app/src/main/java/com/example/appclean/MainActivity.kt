package com.example.appclean

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.appclean.ui.theme.AppCleanTheme
import com.example.appclean.worker.ClearAppDataWorker
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val odiPackageName ="package:com.odibet.app"
    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        if (!dpm.isAdminActive(adminComponentName)) {
            enableDeviceAdmin()
        } else {
            clearAppData(odiPackageName)
        }
        setContent {
            AppCleanTheme {

            }
        }
    }

    private fun enableDeviceAdmin() {
        val intent = android.content.Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName)
        startActivityForResult(intent, 123)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun clearAppData(packageName: String) {
        val executor = Executors.newSingleThreadExecutor()
        dpm.clearApplicationUserData(
            adminComponentName,
            packageName,
            executor
        ) { pkg, succeeded ->
            executor.shutdown()
            if (succeeded) {
                showToast("Successfully cleared data for $packageName")
            } else {
                showToast("Failed to clear data for $packageName")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}



