package com.olayg.onlykats.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.olayg.onlykats.model.Breed
import com.olayg.onlykats.model.Kat
import com.olayg.onlykats.model.Settings
import com.olayg.onlykats.model.request.Queries
import com.olayg.onlykats.repo.KatRepo
import com.olayg.onlykats.util.ApiState
import com.olayg.onlykats.util.EndPoint
import com.olayg.onlykats.util.PageAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//TODO: find a way to check to see if dataStore has cached breeds/categories before making request
class KatViewModel : ViewModel() {

    val TAG = "KatViewModel"

    private val _katState = MutableLiveData<ApiState<List<Kat>>>()
    val katState: LiveData<ApiState<List<Kat>>> get() = _katState

    private val _breedState = MutableLiveData<ApiState<List<Breed>>>()
    val breedState: LiveData<ApiState<List<Breed>>> get() = _breedState

    private val _settingsState = MutableLiveData<ApiState<Settings>>()
    val settingsState: LiveData<ApiState<Settings>> get() = _settingsState

    val stateUpdated = MediatorLiveData<Unit>().apply {
        addSource(_katState) { value = Unit }
        addSource(_breedState) { value = Unit }
        addSource(_settingsState) { value = Unit }
    }

    var queries: Queries? = null

    private var isNextPage = false
    private var currentPage = -1

    fun fetchData(queries: Queries) {
        this.queries = queries
        fetchData(PageAction.FIRST)
    }

    fun fetchData(pageAction: PageAction) {
        if (_katState.value !is ApiState.Loading) queries?.let { query ->
            query.page = pageAction.update(query.page ?: -1)
            val shouldFetchPage = isNextPage || pageAction == PageAction.FIRST
            if (shouldFetchPage) {
                currentPage = query.page!!
                when (query.endPoint) {
                    EndPoint.IMAGES -> getImages(query)
                    EndPoint.BREEDS -> getBreeds()
                }
            }
        }
    }

    private fun getImages(queries: Queries) {
        viewModelScope.launch {
            KatRepo.getKatState(queries).collect { katState ->
                isNextPage = katState !is ApiState.EndOfPage
                _katState.postValue(katState)
            }
        }
    }

    private fun getBreeds() {
        viewModelScope.launch {
            KatRepo.getBreedState().collect { breedState ->
                isNextPage = breedState !is ApiState.EndOfPage
                _breedState.postValue(breedState)
            }
        }
    }

    private fun PageAction.update(page: Int) = when (this) {
        PageAction.FIRST -> 0
        PageAction.NEXT -> page.inc()
        PageAction.PREV -> if (page > 0) page.dec() else page
    }


    fun getDropdownOptions() {
        viewModelScope.launch(Dispatchers.IO) {
            KatRepo.getSettingsState().collect {
                _settingsState.postValue(it)
            }
        }
    }

}
