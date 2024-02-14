package com.chtibizoux.adeapp.ui.login

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.User
import java.net.URI
import java.net.URISyntaxException

class LoginViewModel : ViewModel() {
    var link by mutableStateOf("")
        private set

    @get:StringRes
    var linkError: Int? by mutableStateOf(null)
        private set

    fun updateLink(link: String) {
        this.link = link
        checkLink()
    }

    fun checkLink(): User? {
        try {
            val url = URI(link)
            if (!url.path.startsWith("/direct/") || !url.path.startsWith("/jsp/") || !url.path.startsWith("/ade/")) {
                linkError = R.string.not_an_ade_url
            }
            val dataMatchResult = Regex("data=([^&]+)").find(url.query)
            if (dataMatchResult == null) {
                linkError = R.string.no_data_not_supported
                return null
            }
            val (data) = dataMatchResult.destructured

            val projectIdMatchResult = Regex("projectId=([^&]+)").find(url.query)
            if (projectIdMatchResult == null) {
                linkError = R.string.projectId_selection_not_supported
                return null
            }
            val (projectIdString) = projectIdMatchResult.destructured
            val projectId = projectIdString.toIntOrNull()
            if (projectId == null) {
                linkError = R.string.wrong_projectId
                return null
            }

            val resourcesMatchResult = Regex("resources=([^&]+)").find(url.query)
            if (resourcesMatchResult == null) {
                linkError = R.string.resourceId_selection_not_supported
                return null
            }
            val (resources) = resourcesMatchResult.destructured
            if (resources.contains(",")) {
                linkError = R.string.resourceId_selection_not_supported
                return null
            }
            val resourceId = resources.toIntOrNull()
            if (resourceId == null) {
                linkError = R.string.wrong_resourceId
                return null
            }

            val baseURL = url.scheme + "//" + url.authority
            return User(baseURL, projectId, resourceId, data)
        } catch (e: URISyntaxException) {
            linkError = R.string.invalid_url
            return null
        }
    }
}
