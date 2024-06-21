package bangkit.capstone.waterwise.news

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.databinding.NewsItemHorizontalBinding
import com.bumptech.glide.Glide

class NewsHorizontalAdapter:  ListAdapter<News, NewsHorizontalAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsHorizontalAdapter.NewsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class NewsViewHolder(private val binding: NewsItemHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(news: News) {
            with(binding) {
                if(news.urlToImage != null) {
                    Glide.with(itemView)
                        .load(news.urlToImage)
                        .into(newsImage)
                }

                newsTitle.text = news.title ?: "-"
                newsSource.text = news.source?.name ?: "Unknown"
            }

            itemView.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                itemView.context.startActivity(intent)
            }
        }
    }
}