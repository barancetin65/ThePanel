package com.thepanel

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.thepanel.admin.PanelDeviceAdminReceiver
import com.thepanel.ui.ThePanelApp
import com.thepanel.ui.theme.ThePanelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { }
            
            val adminLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { }

            LaunchedEffect(Unit) {
                // Request permissions
                val permissions = buildList {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        add(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    ) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                if (permissions.isNotEmpty()) permissionLauncher.launch(permissions.toTypedArray())

                // Request Device Admin
                val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val adminComponent = ComponentName(this@MainActivity, PanelDeviceAdminReceiver::class.java)
                if (!dpm.isAdminActive(adminComponent)) {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "The Panel'in kiosk ozelliklerini yonetmesi icin cihaz yonetici izni gereklidir.")
                    }
                    adminLauncher.launch(intent)
                }
            }
            ThePanelApp()
        }
    }

    override fun onResume() {
        super.onResume()
        runCatching {
            val kioskEnabled = getSharedPreferences("kiosk_state", Context.MODE_PRIVATE).getBoolean("enabled", false)
            if (kioskEnabled) {
                startLockTask()
            } else {
                stopLockTask()
            }
        }
    }
}
