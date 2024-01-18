package com.chtibizoux.adeapp.data.xml

import com.chtibizoux.adeapp.R

data class ResourceTree(
    val categories: List<Category>
)

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
