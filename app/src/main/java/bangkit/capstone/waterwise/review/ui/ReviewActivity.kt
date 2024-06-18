package bangkit.capstone.waterwise.review.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import bangkit.capstone.waterwise.databinding.ActivityReviewBinding
import bangkit.capstone.waterwise.review.Type

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    private fun generateDummyData(){
        val data = mutableListOf<Type.Review>()

        for (i in 1..100){
            if (i%2 == 0){
                data.add(
                    Type.Review(
                        id = i,
                        description = "Description $i",
                        predictionByImage = Type.PredictionByImage(
                            id = i,
                            imageUrl = "https://www.google.com"
                        ),
                        predictionByData = null,
                        type = Type.PredictionType.PREDICTION_BY_IMAGE,
                        createdAt = "2021-08-01"
                    )
                )
            } else {
                data.add(
                    Type.Review(
                        id = i,
                        description = "Description $i",
                        predictionByImage = null,
                        predictionByData = Type.PredictionByData(
                            id = i,
                            solids = 1.0f,
                            turbidity = 1.0f,
                            carbon = 1.0f,
                            chlorine = 1.0f,
                            sulfate = 1.0f,
                            ph = 1.0f
                        ),
                        type = Type.PredictionType.PREDICTION_BY_DATA,
                        createdAt = "2021-08-01"
                    )
                )
            }
        }
    }
}