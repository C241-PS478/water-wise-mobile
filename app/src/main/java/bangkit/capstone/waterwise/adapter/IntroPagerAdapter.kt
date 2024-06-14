package bangkit.capstone.waterwise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ItemIntroPageBinding
import bangkit.capstone.waterwise.datastore.model.IntroItem
import com.bumptech.glide.Glide

class IntroPagerAdapter(private val introItems: List<IntroItem>) :
    RecyclerView.Adapter<IntroPagerAdapter.IntroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val binding = ItemIntroPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        holder.bind(introItems[position])
    }

    override fun getItemCount(): Int = introItems.size

    class IntroViewHolder(private val binding: ItemIntroPageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(introItem: IntroItem) {
            Glide.with(binding.imageView2.context)
                .load(introItem.image)
                .into(binding.imageView2)
            binding.introTitle.text = introItem.title
            binding.introDesc.text = introItem.description
        }
    }
}
