package bangkit.capstone.waterwise.news

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.databinding.NewsItemBinding
import bangkit.capstone.waterwise.utils.Helper
import com.bumptech.glide.Glide

class NewsAdapter: PagingDataAdapter<News, NewsAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
        Log.d("NewsAdapter", "onBindViewHolder: $item")
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

    inner class ViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(news: News) {
            with(binding) {
                if(news.urlToImage != null) {
                    Glide.with(itemView)
                        .load(news.urlToImage)
                        .into(newsImage)
                }

                val publishedAt = Helper.getParsedDateToLocale(news.publishedAt!!)
                newsTitle.text = news.title ?: "-"
                newsSource.text = news.source?.name ?: "Unknown"
                newsPublishedDate.text = publishedAt
            }

            itemView.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                itemView.context.startActivity(intent)
            }
        }
    }
}