package com.chtibizoux.adeapp.ui.home.resourceSelector

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Settings
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.ResourceTree
import com.chtibizoux.adeapp.ui.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(resources: List<Resource>) : ViewModel() {
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()


    private val _resourcesList = MutableStateFlow(resources)
    val resourceList = searchText.combine(_resourcesList) { text, resources ->
        if (text.isBlank()) {
            resources
        }
        resources.filter { resource ->
            resource.name.uppercase().contains(text.trim().uppercase())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _resourcesList.value
    )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onActiveChange(isSearching: Boolean) {
        _isSearching.value = isSearching
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }
}

class SearchViewModelFactory(
    private val resources: ResourceTree
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(resources.toList()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
