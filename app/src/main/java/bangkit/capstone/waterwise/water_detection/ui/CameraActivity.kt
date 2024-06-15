package bangkit.capstone.waterwise.water_detection.ui

import android.content.Intent
import android.os.Bundle
import android.util.Size
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>
    private var isFlashOn = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        with(binding) {
            btnCapture.setOnClickListener {
                val intent = Intent(this@CameraActivity, DetectResult::class.java)
                startActivity(intent)
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

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraContainer.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().setTargetResolution(Size(480, 720)).build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                )

                isFlashOn.observe(this) {
                    camera.cameraControl.enableTorch(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

//    private fun takePhoto() {
//
//    }
}