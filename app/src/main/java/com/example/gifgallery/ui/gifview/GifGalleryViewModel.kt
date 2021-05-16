package com.example.gifgallery.ui.gifview

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.Result
import com.example.gifgallery.data.source.GifRepository
import com.example.gifgallery.utils.SingleLiveEvent
import com.example.gifgallery.utils.network.InternetConnectionManager
import com.example.gifgallery.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch


class GifGalleryViewModel(
    private val gifGalleryRepository: GifRepository,
    private val internetConnectionManager: InternetConnectionManager
) : ViewModel() {

    companion object {
        var PAGE_LIMIT = 50
        var CURRENT_INDEX = 0
    }

    val _currentPage = ObservableField<Int>(0)
    val _currentSearch = ObservableField<String>("")
    val isSearchEmpty = ObservableField<Boolean>(true)
    val isInternetAvailable = ObservableField(true)

    val isDataLoadingError = MutableLiveData<Boolean>(false)

    val _itemClicked = SingleLiveEvent<String>()
    val itemClicked: LiveData<String> = _itemClicked

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _gifs: LiveData<List<GifModelDataItem>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate && _currentSearch.get()!!.isNotEmpty()) {
            viewModelScope.launch {
                handleResponseWithError(gifGalleryRepository.getGifs(true, _currentSearch.get()!!,
                    _currentPage.get()!!, PAGE_LIMIT))
                _dataLoading.value = false
            }
        } else
            _dataLoading.value = false

        gifGalleryRepository.observeGifs(_currentSearch.get()!!).map {
            if (it is Result.Success) {
                isDataLoadingError.value = false
                    it.data

            } else if(it is Result.Error){
                _error.postValue(it.exception)
                isDataLoadingError.value = true
                emptyList()
            }
            else
                emptyList()
        }
    }

    val items: LiveData<List<GifModelDataItem>> = _gifs

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_gifs) {
        it.isEmpty()
    }

    fun onItemClick(gifModelDataItem: GifModelDataItem){
        _itemClicked.postValue(gifModelDataItem.images.fixedWithSmall.url ?: "")
    }

    fun refresh(refresh: Boolean) {
        loadGifs(refresh)
    }

    fun onSearchTextChanged(query: String){
        _dataLoading.postValue(true)
        _currentPage.set(0)
        CURRENT_INDEX = 0
        _currentSearch.set(query)
        _forceUpdate.postValue(true)
        isSearchEmpty.set(query.isEmpty())
    }

    private fun loadGifs(isLoad: Boolean) {
        if(!internetConnectionManager.hasInternetConnection()) {
            isInternetAvailable.set(false)
        }
        else {
            if(isInternetAvailable.get() == false) isInternetAvailable.set(true)
            _dataLoading.postValue(isLoad)
            _forceUpdate.postValue(isLoad)

        }
    }

    private fun handleResponseWithError(response: Result<List<GifModelDataItem>>): LiveData<List<GifModelDataItem>> {
        return when (response) {
            is Result.Success -> {
                isDataLoadingError.value = false
                MutableLiveData(response.data) as LiveData<List<GifModelDataItem>>
            }
            is Result.Error -> {
                isDataLoadingError.value = true
                _error.postValue(response.exception)
                MutableLiveData( response.exception) as LiveData<List<GifModelDataItem>>
            }
            is Result.Loading -> MutableLiveData( null)
        }
    }

    fun listScrolled(lastVisibleItemPosition: Int, totalItemCount: Int) {
         if ( CURRENT_INDEX <= totalItemCount
            && lastVisibleItemPosition > totalItemCount - 30
            && !_dataLoading.value!!
        ) {
            viewModelScope.launch {
                _currentPage.set(_currentPage.get()!!.plus(PAGE_LIMIT))
                CURRENT_INDEX = _currentPage.get()!!
                loadGifs(true)
            }
        }
    }
}