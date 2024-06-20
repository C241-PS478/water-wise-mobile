package bangkit.capstone.waterwise.utils

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.exifinterface.media.ExifInterface
import bangkit.capstone.waterwise.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Helper {
    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    fun dialogBuilder (context: Context, layout: Int, isCancelable: Boolean = false): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(isCancelable)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layout)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = android.R.transition.fade
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    fun infoDialog(
        context: Context,
        message: String,
        alignment: Int = Gravity.CENTER
    ): Dialog {
        val dialog = dialogBuilder(context, R.layout.dialog_info_layout, false)
        val tvMessage = dialog.findViewById<TextView>(R.id.info_dialog_message)
        val closButton = dialog.findViewById<TextView>(R.id.close_info_dialog_btn)

        tvMessage.text = message
        tvMessage.gravity = alignment

        closButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun loadingDialog (context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loader_dialog)
        dialog.setCancelable(false)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }

    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun getImageOrientation(file: File): Int {
        val ei = ExifInterface(file)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    fun formatToDecimal(number: Float): Float {
        val df = DecimalFormat("#.##")
//        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toFloat()
    }

    fun isGPSEnabled (context: Context): Boolean {
        val gpsService = getSystemService(context, LocationManager::class.java) as LocationManager
        val isEnabled = gpsService.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return isEnabled
    }

    private const val TIME_STAMP_FORMAT = "MMddyyyy"
    private val timeStamp: String = SimpleDateFormat(
        TIME_STAMP_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun createCustomTempFile(context: Context, ext: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ext, storageDir)
    }

    fun uriToFile(selectedImg: Uri, context: Context, ext: String): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context, ext)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private const val SIMPLE_DATE_FORMAT = "dd MMM yyyy HH.mm"
    private val simpleDate = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault())

    private fun getSimpleDate(date: Date): String = simpleDate.format(date)

    private fun parseUTCDate(timestamp: String): Date {
        return try {
            val formatter = SimpleDateFormat(TIME_STAMP_FORMAT, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(timestamp) as Date
        } catch (e: ParseException) {
            getCurrentDate()
        }
    }

    private fun getCurrentDate(): Date {
        return Date()
    }

    fun getParsedDateToLocale(timestamp: String): String {
        val date: Date = parseUTCDate(timestamp)
        return getSimpleDate(date)
    }

    @Suppress("DEPRECATION")
    fun isHasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun bitmapToImage(bitmap: Bitmap, context: Context): File {
        val file = createCustomTempFile(context, ".jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }
}