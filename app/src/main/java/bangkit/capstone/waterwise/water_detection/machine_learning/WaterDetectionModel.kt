package bangkit.capstone.waterwise.water_detection.machine_learning

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import bangkit.capstone.waterwise.ml.CleanWaterV2
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

@Suppress("DEPRECATION")
class WaterDetectionModel(val context: Context) {
    private val cleanWaterClassifierModel = CleanWaterV2.newInstance(context)

    fun classify(bitmap: Bitmap): FloatArray  {
        val image = normalizeImage(bitmap)
        val output = cleanWaterClassifierModel.process(image)

        cleanWaterClassifierModel.close()
        return output.classificationAsTensorBuffer.floatArray
    }

    private fun normalizeImage(bitmap: Bitmap): TensorImage {
        Log.d("normalizeImage", "Bitmap dimensions: width=${bitmap.width}, height=${bitmap.height}")

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .build()

        val processedImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        Log.d("normalizeImage", "Processed image dimensions: width=${processedImage.width}, height=${processedImage.height}")
        return processedImage
    }
}