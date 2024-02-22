package com.chtibizoux.adeapp.ui.home.resourceSelector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.ResourceTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ResourceSelectorViewModel : ViewModel() {
    private val _resourceTree = MutableStateFlow<ResourceTree?>(null)
    val resourceTree = _resourceTree.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()


    val resourceList = searchText.combine(resourceTree) { text, resourceTree ->
        val resources = resourceTree?.toList() ?: listOf()
        if (text.isBlank()) {
            resources
        }
        resources.filter { resource ->
            resource.name.uppercase().contains(text.trim().uppercase())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = resourceTree.value?.toList() ?: listOf()
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

    fun setResources(resources: ResourceTree) {
        _resourceTree.value = resources
    }
}