package com.chtibizoux.adeapp.data.xml

import com.chtibizoux.adeapp.R

data class ResourceTree(
    val categories: List<Category>
) {

    private fun getAllObjects(root: Resource): List<Resource> {
        val result = mutableListOf(root)
        for (child in root.children) {
            result.addAll(getAllObjects(child))
        }
        return result
    }

    fun toList(): List<Resource> {
        val resources: MutableList<Resource> = mutableListOf()
        for (category in this.categories) {
            for (resource in category.resources) {
                resources.addAll(getAllObjects(resource))
            }
        }
        return resources
    }
}

data class Category(
    val category: String,
    val resources: List<Resource>
) {
    val name
        get() = when (category) {
            "trainee" -> R.string.students
            "instructor" -> R.string.teachers
            "classroom" -> R.string.classrooms
            "equipment" -> R.string.equipments
            "category5" -> R.string.lessons
            else -> null
        }
}

data class Resource(
    val id: Int,
    val name: String,
    val children: List<Resource>
)

fun getLeaves(resources: List<Resource>): List<Resource> {
    val leaves: MutableList<Resource> = mutableListOf()
    resources.forEach {
        if (it.children.isEmpty()) {
            leaves.add(it)
        } else {
            leaves.addAll(getLeaves(it.children))
        }
    }
    return leaves
}

fun getAllChildren(resources: List<Resource>): List<Resource> {
    return resources + resources.flatMap { getAllChildren(it.children) }
}
