package bangkit.capstone.waterwise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.model.IntroItem
import kotlinx.android.synthetic.main.item_intro_page.view.*

class IntroPagerAdapter(private val introItems: List<IntroItem>) :
    RecyclerView.Adapter<IntroPagerAdapter.IntroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_intro, parent, false)
        return IntroViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        holder.bind(introItems[position])
    }

    override fun getItemCount(): Int = introItems.size

    class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(introItem: IntroItem) {
            itemView.imageView.setImageResource(introItem.image)
            itemView.textViewTitle.text = introItem.title
            itemView.textViewDescription.text = introItem.description
        }
    }
}
