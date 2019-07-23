package com.example.android.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("category")
data class CategoryWithChildren(val name: String, @JsonProperty("image_main_mvp_jpeg_high_url") val image: String?) {

    @Id
    lateinit var id: String
    @Relationship("children")
    var children: MutableList<CategoryWithChildren>? = null

    fun flatten(parentId: String? = null, depth: Int = 0, maxDepth: Int = 2): List<Category> {
        val categoryImage =
                when (depth) {
                    0 -> image
                    1 -> "https://static.bonami.cz/app-category-l2/cz/$id.jpg"
                    else -> null
                }
        val category = Category(id, parentId, children?.size ?: 0, depth, name, categoryImage)
        val list = mutableListOf(category)

        if (depth < maxDepth || maxDepth == -1) {
            children?.let {
                it.forEach { categoryWithChildren ->
                    list.addAll(categoryWithChildren.flatten(category.id, depth + 1, maxDepth))
                }
            }
        }
        return list
    }
}

@Entity(indices = [(Index("parentId", "parentId"))])
data class Category(@PrimaryKey val id: String, val parentId: String?, val childCount: Int, val depth: Int, val name: String, val image: String?)