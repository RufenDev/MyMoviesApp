package com.example.mymoviesapp.adapter.seemore

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient.IMAGE_PATH_W185
import com.example.mymoviesapp.api.DetailsItem
import com.example.mymoviesapp.databinding.ItemSeeMoreBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class SeeMoreViewHolder(view:View):RecyclerView.ViewHolder(view) {

    private val binding = ItemSeeMoreBinding.bind(view)

    fun render(item: DetailsItem, onItemClicked: (DetailsItem) -> Unit){
        binding.tvSeeMoreTitle.text = item.title

        if(item.subtitle.isNotEmpty()){
            binding.tvSeeMoreSubtitle.text = item.subtitle
            binding.tvSeeMoreSubtitle.visibility = View.VISIBLE
        }

        Picasso.get()
            .load(IMAGE_PATH_W185 + item.imagePath)
            .into(
                binding.ivSeeMoreImage,
                object : Callback{
                    override fun onSuccess() {
                        binding.loadingSeeMoreItem.visibility = View.GONE
                        binding.ivSeeMoreImage.visibility = View.VISIBLE
                    }
                    override fun onError(e: Exception?) {
                        binding.ivSeeMoreImage.setImageResource(R.drawable.image_not_found)
                        binding.loadingSeeMoreItem.visibility = View.GONE
                        binding.ivSeeMoreImage.visibility = View.VISIBLE
                    }
                }
            )
        binding.ivSeeMoreImage.setOnClickListener {
            onItemClicked(item)
        }
    }
}