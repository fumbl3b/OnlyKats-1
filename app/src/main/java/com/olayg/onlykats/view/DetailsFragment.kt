package com.olayg.onlykats.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.olayg.onlykats.R
import com.olayg.onlykats.adapter.StatAdapter
import com.olayg.onlykats.databinding.FragmentDetailBinding
import com.olayg.onlykats.model.Breed
import com.olayg.onlykats.util.loadWithGlide

class DetailsFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val args: DetailsFragmentArgs by navArgs()
    private val statAdapter by lazy { StatAdapter() }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailBinding.inflate(layoutInflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val breed = args.breedId  //TODO: rename this
        initViews(breed, view)

        binding.tvBreedDescription.text = breed.description
    }

    private fun initViews(breed: Breed, view: View) = with(binding) {
        topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        topAppBar.title = "The ${breed.name}"
        breed.image?.url?.let {
            binding.ivBreedDetail.loadWithGlide(it)
        }
        if(!breed.altNames.isNullOrEmpty()) tvBreedAltNames.text = "aka ${breed.altNames}"
        else tvBreedAltNames.visibility = View.GONE

        tvBreedLifeSpan.text = "Lifespan: ${breed.lifeSpan} years"
        tvBreedOrigin.text = "Origin: ${breed.origin}"
        tvBreedTemperment.text = breed.temperament
        breedWikiLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(breed.wikipediaUrl))
            startActivity(browserIntent)
        }

        val statList = listOfNotNull(
            breed.adaptability?.let { "Adaptability" to it },
            breed.affectionLevel?.let { "Affection Level" to it },
            breed.childFriendly?.let { "Child Friendly" to it },
            breed.dogFriendly?.let { "Dog Friendly" to it },
            breed.energyLevel?.let { "Energy Level" to it },
            breed.intelligence?.let { "Intelligence" to it },
        )

        rvBreedStats.adapter = statAdapter
        statAdapter.addStats(statList)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}