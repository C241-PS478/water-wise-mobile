package bangkit.capstone.waterwise.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Helper {
    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}