package bangkit.capstone.waterwise.water_detection.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityCameraBinding
import bangkit.capstone.waterwise.utils.Helper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>
    private var isFlashOn = MutableLiveData<Boolean>(false)
    private var isLoading = MutableLiveData<Boolean>(false)

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        with(binding) {
            btnCapture.setOnClickListener {
                takePhoto()
            }

            btnClose.setOnClickListener {
                finish()
            }

            btnFlash.setOnClickListener {
                isFlashOn.value = !isFlashOn.value!!
            }
        }

        isFlashOn.observe(this) {
            if (it) {
                binding.btnFlash.setImageResource(R.drawable.flash_on)
            } else {
                binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
            }
        }

        isLoading.observe(this) {
            if (it) loadingDialog.show() else loadingDialog.dismiss()
        }

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraContainer.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                isFlashOn.observe(this) {
                    camera.cameraControl.enableTorch(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        isLoading.value = true
        val imageCapture = imageCapture ?: return
        val tempFile = createCustomTempFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    isLoading.value = false
                    Helper.infoDialog(this@CameraActivity, "Failed to take photo. : $exc").show()
                    Log.e(TAG, "ERROR: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent(this@CameraActivity, DetectResultActivity::class.java)
                    intent.putExtra(DetectResultActivity.PHOTO_RESULT, tempFile)
                    intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    startActivity(intent)

                    isLoading.value = false
                    finish()
                }
            }
        )
    }

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    companion object {
        const val FILENAME_FORMAT = "MMddyyyy"
        const val TAG = "CameraActivity"
    }
}