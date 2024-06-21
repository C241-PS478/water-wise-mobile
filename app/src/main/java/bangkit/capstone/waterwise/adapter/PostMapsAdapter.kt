package bangkit.capstone.waterwise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.FragmentDetailMapsBinding
import bangkit.capstone.waterwise.ui.main.ListPostItem
import com.bumptech.glide.Glide

class PostMapsAdapter(private val listPost: List<ListPostItem>) : RecyclerView.Adapter<PostMapsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = FragmentDetailMapsBinding.bind(view)
        fun bind(post: ListPostItem) {
            binding.authorName.text = post.authorName
            binding.datePosted.text = post.dateCreated
            binding.cleanlinessPercentageResult.text = post.prediction?.times(100)?.toInt().toString() + "%"
            binding.postCaption.text = post.description
            Glide.with(binding.waterImage.context)
                .load(post.predictionImageUrl)
                .into(binding.waterImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_detail_maps, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listPost[position])
    }

    override fun getItemCount(): Int = listPost.size
}