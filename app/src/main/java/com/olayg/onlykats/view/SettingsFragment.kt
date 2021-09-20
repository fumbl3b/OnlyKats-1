package com.olayg.onlykats.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.olayg.onlykats.R
import com.olayg.onlykats.databinding.FragmentSettingsBinding
import com.olayg.onlykats.model.Settings
import com.olayg.onlykats.model.request.Queries
import com.olayg.onlykats.util.ApiState
import com.olayg.onlykats.util.EndPoint
import com.olayg.onlykats.util.PreferenceKeys
import com.olayg.onlykats.viewmodel.KatViewModel
import kotlinx.coroutines.flow.map

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val katViewModel by activityViewModels<KatViewModel>()


    private var breedList: List<Pair<String?, String>> = mutableListOf()
    private var categoryList: List<Pair<String?, Int?>> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // check to set the correct filters based on endpoint
        if (katViewModel.queries?.endPoint != null) viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            view.context.dataStore.data.map { preferences ->
                preferences[PreferenceKeys.ENDPOINT]?.let {
                    Queries(
                        endPoint = EndPoint.valueOf(it),
                        limit = preferences[PreferenceKeys.LIMIT] ?: 10,
                        page = 0,
                        breedId = "abys",
                        categoryIds = 1
                    )
                }
            }
        }

        initView()
        initObservers()
        initEndpointDropdown()
    }

    override fun onResume() {
        super.onResume()
        initEndpointDropdown()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() = with(binding) {

        // Maybe will be redundant with PreferenceStore implemented
        katViewModel.queries?.let { queries ->
            sliderLimit.value = queries.limit.toFloat()
            queries.endPoint?.let {
                etEndpoint.setText(it.name)
                etEndpoint.setSelection(it.ordinal)
            }
            //TODO: add the categories and breeds
        }
        sliderLimit.addOnChangeListener { _, _, _ -> toggleApply() }

        // call view model to get data for settings

        katViewModel.getDropdownOptions()

        btnApply.setOnClickListener {
            val queries = getKatQueries()

            // save current query to the PreferenceStore
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                it.context.dataStore.edit { settings ->
                    queries.endPoint?.name?.let {
                        settings[PreferenceKeys.ENDPOINT] = it
                    }
                    queries.limit?.let {
                        settings[PreferenceKeys.LIMIT] = it
                    }
                }
            }

            katViewModel.fetchData(queries)
            findNavController().navigateUp()
        }
    }

    private fun initObservers() = with(katViewModel) {
        stateUpdated.observe(viewLifecycleOwner) {
            toggleApply()
        }
        settingsState.observe(viewLifecycleOwner) {
            if (it is ApiState.Success) initSettingsDropdowns(it.data)
        }
    }

    private fun initEndpointDropdown() = with(binding.etEndpoint) {
        katViewModel.queries?.endPoint?.let {
            setText(it.name)
            setSelection(it.ordinal)
        }
        setAdapter(ArrayAdapter(context, R.layout.item_endpoint, EndPoint.values().map { it.name }))
        setOnItemClickListener { _, view, _, _ ->
            val selectedEndpointText = (view as MaterialTextView).text.toString()
            when (EndPoint.valueOf(selectedEndpointText)) {
                EndPoint.IMAGES -> {
                    toggleImagesView(true)
                    toggleBreedsView(false)
                }
                EndPoint.BREEDS -> {
                    toggleBreedsView(true)
                    toggleImagesView(false)
                }
            }
            toggleApply()
        }
    }

    private fun initSettingsDropdowns(settings: Settings) = with(binding) {

        breedList = settings.breeds?.map { Pair(it.name, it.id) }!!
        categoryList = settings.categories?.map { Pair(it.name, it.id) }!!

        val breedAdapter = ArrayAdapter(
            breedTextView.context,
            R.layout.item_category,
            breedList?.map { it.first }!!
        )
        (breedTextView as? AutoCompleteTextView)?.setAdapter(breedAdapter)

        val categoryAdapter = ArrayAdapter(
            categoryTextView.context,
            R.layout.item_category,
            categoryList?.map { it.first }!!
        )
        (categoryTextView as? AutoCompleteTextView)?.setAdapter((categoryAdapter))

        breedTextView.setOnItemClickListener { _, view, _, _ ->
            toggleApply()
        }

        categoryTextView.setOnItemClickListener { _, view, _, _ ->
            toggleApply()
        }
    }

    private fun toggleImagesView(show: Boolean) = with(binding) {
        imagesEndpointGroup.visibility = if (show) View.VISIBLE
        else View.GONE
    }

    private fun toggleBreedsView(show: Boolean) = with(binding) {
        //TODO: implement toggleBreedsView
    }

    private fun toggleApply() {
        binding.btnApply.isVisible = validateQuery()
    }

    private fun validateQuery(): Boolean {

        val newQuery = getKatQueries()
        Log.d("newQuery", "validateQuery: $newQuery")

        // if VM already has queries else return true if breeds is selected or limit has been set above 10
        return katViewModel.queries?.let {
            // return true if there's a new endpoint or the limit changed to a valid new limit
            return@let it.endPoint != newQuery.endPoint
                    || (it.limit != newQuery.limit || newQuery.limit >= 10)
                    || it.breedId != newQuery.breedId
                    || it.categoryIds != newQuery.categoryIds
        } ?: (newQuery.endPoint == EndPoint.BREEDS
                || newQuery.limit >= 10)
    }

    private fun getKatQueries(): Queries {
        val endpoint = binding.etEndpoint.text.toString().run {
            if (isNotBlank()) EndPoint.valueOf(this) else null
        }
        val limit = binding.sliderLimit.value.toInt()
        val page = katViewModel.queries?.page

        val categoryStr = binding.categoryTextView.text.toString()
        val categoryIds = categoryList.firstOrNull { it.first == categoryStr }?.second

        val breedString = binding.breedTextView.text.toString()
        val breedId = breedList.firstOrNull { it.first == breedString }?.second

        return Queries(
            endpoint,
            limit,
            page,
            breedId,
            categoryIds
        )
    }
}