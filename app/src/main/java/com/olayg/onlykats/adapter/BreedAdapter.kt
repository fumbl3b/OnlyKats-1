package com.olayg.onlykats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olayg.onlykats.databinding.ItemBreedBinding
import com.olayg.onlykats.model.Breed
import com.olayg.onlykats.util.loadWithGlide

/**
 * ListView - loads all objects into memory
 * RecyclerView - Leverages the ViewHolder Pattern to optimizing scrolling and memory consumption
 * ListAdapter - Same as Recyclerview but we don't have to use the notify methods to update the adapter
 */
class BreedAdapter(
    private val breedList: MutableList<Breed> = mutableListOf()
) : RecyclerView.Adapter<BreedAdapter.BreedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BreedViewHolder.getInstance(parent)

    override fun onBindViewHolder(holder: BreedViewHolder, position: Int) {
        holder.loadBreed(breedList[position], position + 1)
    }

    override fun getItemCount(): Int {
        return breedList.size
    }

    fun clear() {
        val listSize = breedList.size
        breedList.clear()
        notifyItemRangeRemoved(0, listSize)
    }

    fun updateList(breeds: List<Breed>) {
        if (breeds.lastOrNull() != breedList.lastOrNull()) {
            val positionStart = breedList.size
            breedList.addAll(breeds)
            notifyItemRangeInserted(positionStart, breeds.size)
        }
    }

    class BreedViewHolder(
        private val binding: ItemBreedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun loadBreed(breed: Breed, position: Int) = with(binding) {
            tvBreed.text = "${position}. ${breed.name}"
            breed.image?.url?.let { ivBreedImage.loadWithGlide(it) }
        }

        companion object {
            fun getInstance(parent: ViewGroup): BreedViewHolder {
                val binding = ItemBreedBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return BreedViewHolder(binding)
            }
        }
    }

}
