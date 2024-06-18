package bangkit.capstone.waterwise.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ReviewItemByImageBinding
import bangkit.capstone.waterwise.review.Types.PredictionType
import bangkit.capstone.waterwise.review.Types.Review

class ReviewListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data = mutableListOf<Review>()

    fun setData(data: List<Review>){
        this.data.clear()
        this.data.addAll(data)
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]
        return when(item.type){
            PredictionType.PREDICTION_BY_IMAGE -> 1
            PredictionType.PREDICTION_BY_DATA -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item_by_image, parent, false)
                return ViewHolderByImage(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item_by_data, parent, false)
                return ViewHolderByData(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when(item.type){
            PredictionType.PREDICTION_BY_IMAGE -> (holder as ViewHolderByImage).bind(item)
            PredictionType.PREDICTION_BY_DATA -> (holder as ViewHolderByData).bind(item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolderByImage(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ReviewItemByImageBinding.bind(itemView)

        fun bind(item: Review){
            binding.apply {

            }
        }
    }

    inner class ViewHolderByData(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ReviewItemByImageBinding.bind(itemView)

        fun bind(item: Review){
            binding.apply {
                // bind data
            }
        }
    }
}