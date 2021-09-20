package com.olayg.onlykats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.olayg.onlykats.R
import com.olayg.onlykats.databinding.FragmentSettingsBinding
import com.olayg.onlykats.model.request.Queries
import com.olayg.onlykats.util.ApiState
import com.olayg.onlykats.util.EndPoint
import com.olayg.onlykats.viewmodel.KatViewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding!!
    private val katViewModel by activityViewModels<KatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSettingsBinding.inflate(inflater, container, false)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // setup
    private fun initView() = with(binding) {
        val currentQueries = katViewModel.queries
            ?: Queries(null, 0, 0, null, null)
        etEndpoint.setText(currentQueries.endPoint.toString())
        sliderLimit.value = currentQueries.limit.toFloat()

        breedTextView.setText(currentQueries.breedId.toString()) // TODO: fix this logic
        categoryTextView.setText(currentQueries.categoryIds.toString()) // TODO: fix this logic

        katViewModel.settingsState.observe(viewLifecycleOwner) {
            if (it is ApiState.Success) initSettingsDropDowns()
        }
        setListeners()
    }

    private fun initSettingsDropDowns() = with(binding) {
        val breedAdapter = ArrayAdapter(
            breedTextView.context,
            R.layout.item_category,
            katViewModel.breedList?.map { it.first }!!
        )
        (breedTextView as? AutoCompleteTextView)?.setAdapter(breedAdapter)
        val categoryAdapter = ArrayAdapter(
            categoryTextView.context,
            R.layout.item_category,
            katViewModel.categoryList?.map { it.first }!!
        )
        (categoryTextView as? AutoCompleteTextView)?.setAdapter(categoryAdapter)
    }

    private fun setListeners() = with(binding) {
        sliderLimit.setOnClickListener { toggleApply() }
        breedTextView.setOnClickListener { toggleApply() }
        categoryTextView.setOnClickListener { toggleApply() }
    }

    private fun toggleApply() {
        binding.btnApply.isVisible = validateQuery()
    }

    // query logic
    private fun validateQuery(): Boolean {
        val query = getSearchQueries()

        return if (katViewModel.queries == null) {
            query.endPoint == EndPoint.BREEDS
                    || query.limit >= 10
                    || query.categoryIds != null
                    || query.breedId != null
        } else katViewModel.queries.let {
            it?.endPoint != query.endPoint
                    || (it?.limit != query.limit && query.limit >= 10)
                    || it?.breedId != query.breedId
                    || it?.categoryIds != query.categoryIds
        }
    }


    // gui to Queries() class
    private fun getSearchQueries(): Queries = with(binding) {
        val endpoint = etEndpoint.text.toString().run {
            if (isNotBlank()) EndPoint.valueOf(this)
            else null
        }
        val limit = sliderLimit.value.toInt()
        val page = katViewModel.queries?.page
        val breedName = categoryTextView.text.toString()
        val categoryName = categoryTextView.text.toString()

        val breedId =
            katViewModel.breedList?.firstOrNull { it.first == breedName }?.second
        val categoryIds =
            katViewModel.categoryList?.firstOrNull { it.first == categoryName }?.second

        return Queries(
            endpoint,
            limit,
            page,
            breedId,
            categoryIds
        )
    }
}