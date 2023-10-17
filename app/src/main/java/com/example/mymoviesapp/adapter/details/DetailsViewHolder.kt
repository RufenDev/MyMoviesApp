package com.example.mymoviesapp.adapter.details

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.DetailsItem
import com.example.mymoviesapp.databinding.ItemDetailsBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemDetailsBinding.bind(view)

    fun render(item: DetailsItem, onItemClicked: ((DetailsItem) -> Unit)) {

        binding.tvItemDetailsTitle.text = item.title.titleCase()

        if(item.subtitle.isNotEmpty()){
            binding.tvItemDetailsSubtitle.text = item.subtitle.titleCase()

        } else {
            binding.tvItemDetailsSubtitle.visibility = View.GONE
        }

        val imageURL:String = (if(item.isAnActor){
            APIClient.IMAGE_PATH_W185
        } else {
            APIClient.IMAGE_PATH_W342
        }) + item.imagePath.orEmpty()


        Picasso.get().load(imageURL).into(binding.ivItemDetailsImage, object :Callback{
            override fun onSuccess() {
                binding.itemDetailsLoading.visibility = View.GONE
                binding.ivItemDetailsImage.visibility = View.VISIBLE
            }

            override fun onError(e: Exception?) {
                binding.ivItemDetailsImage.setImageResource(R.drawable.image_not_found)

                binding.itemDetailsLoading.visibility = View.GONE
                binding.ivItemDetailsImage.visibility = View.VISIBLE
            }
        })

        itemView.setOnClickListener {
            onItemClicked(item)
        }

    }

    private fun String.titleCase(): String {
        return this.split(" ").joinToString(" ") { str ->
            str.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

}