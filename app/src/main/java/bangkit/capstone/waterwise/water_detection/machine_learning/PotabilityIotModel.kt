package bangkit.capstone.waterwise.water_detection.machine_learning

import android.content.Context
import android.util.Log
import bangkit.capstone.waterwise.ml.PotabilityIotV2
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

@Suppress("DEPRECATION")
class PotabilityIotModel(val context: Context) {
    private val potabilityIotModel = PotabilityIotV2.newInstance(context)

    fun classify(data: FloatArray): FloatArray {
        val inputShape = intArrayOf(1, data.size)
        val tabularData = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        tabularData.loadArray(data)

        val output = potabilityIotModel.process(tabularData)

        potabilityIotModel.close()
        Log.d("output", output.toString())
        return output.classificationAsTensorBuffer.floatArray
    }
}