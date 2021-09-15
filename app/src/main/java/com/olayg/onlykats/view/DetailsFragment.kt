package com.olayg.onlykats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.olayg.onlykats.R
import com.olayg.onlykats.databinding.FragmentDetailBinding
import com.olayg.onlykats.util.loadWithGlide

class DetailsFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val args: DetailsFragmentArgs by navArgs()
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
        initViews()
        breed.image?.url?.let {
            binding.ivBreedDetail.loadWithGlide(it)
        }
        binding.tvDetails.text = "The ${breed.name}"
        if(!breed.altNames.isNullOrEmpty()) binding.tvBreedAltNames.text = "aka ${breed.altNames}"
        else binding.tvBreedAltNames.visibility = View.GONE
        binding.tvBreedDescription.text = breed.description
    }

    private fun initViews() = with(binding) {
        detailBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}