package bangkit.capstone.waterwise.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.adapter.PostMapsAdapter
import bangkit.capstone.waterwise.databinding.FragmentDetailMapsBinding
import bangkit.capstone.waterwise.ui.main.ListPostItem

class DetailFragmentMaps : Fragment() {

    private var _binding: FragmentDetailMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ada
    private var listPost: List<ListPostItem> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DetailAdapter(listPost)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setListPost(listPost: List<ListPostItem>) {
        this.listPost = listPost
        adapter.notifyDataSetChanged()
    }
}