package com.example.android.data.remote.deserializer

import com.example.android.data.model.*
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class FilterDeserializer : JsonDeserializer<Filter> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Filter {
        with(json.asJsonObject) {
            val typeName = get("type").asString.map { if (it.isUpperCase()) "_$it" else it.toUpperCase().toString() }.joinToString("")
            val type = FilterType.values().firstOrNull { it.name == typeName }
            val name = get("name").asString
            val caption = get("caption").asString.capitalize()

            return when (type) {
                FilterType.OPTION_LIST -> deserializeOptionListFilter(name, caption)
                FilterType.TICK_LIST -> deserializeTickListFilter(name, caption)
                FilterType.TICK_LIST_SEARCH -> deserializeTickListSearchFilter(name, caption)
                FilterType.RANGE -> deserializeRangeFilter(name, caption)
                FilterType.COLOR -> deserializeColorFilter(name, caption)
                FilterType.STEPPER -> deserializeStepperFilter(name, caption)
                else -> OptionListFilter(name, caption, emptyList())
            }

        }
    }

    private fun JsonObject.deserializeOptionListFilter(name: String, caption: String): OptionListFilter {
        val values = listOf(get("value").asString)
        val options = get("options").asJsonArray.map { it.asJsonObject.deserializeFilterOption(values) }
        return OptionListFilter(name, caption, options)
    }

    private fun JsonObject.deserializeTickListFilter(name: String, caption: String): TickListFilter {
        val values = get("value").asJsonArray.map { it.asString }
        val options = get("options").asJsonArray.map { it.asJsonObject.deserializeFilterOption(values) }
        return TickListFilter(name, caption, options)
    }

    private fun JsonObject.deserializeTickListSearchFilter(name: String, caption: String): TickListFilter {
        val values = get("value").asJsonArray.map { it.asString }
        val options = get("options").asJsonArray.map { it.asJsonObject.deserializeFilterOption(values) }.sortedBy { it.label }
        return TickListFilter(name, caption, options)
    }

    private fun JsonObject.deserializeColorFilter(name: String, caption: String): ColorFilter {
        val values = get("value").asJsonArray.map { it.asString }
        val options = get("options").asJsonArray.map { it.asJsonObject.deserializeFilterColorOption(values) }
        return ColorFilter(name, caption, options)
    }

    private fun JsonObject.deserializeRangeFilter(name: String, caption: String): RangeFilter {
        val settings = get("settings").asJsonObject.deserializeFilterRangeSettings()
        val value = get("value").asJsonObject.deserializeFilterRangeOption(settings.min, settings.max)
        return RangeFilter(name, caption, value, settings)
    }

    private fun JsonObject.deserializeStepperFilter(name: String, caption: String): StepperFilter {
        val settings = get("settings").asJsonObject.deserializeFilterRangeSettings()
        val value = get("value").asJsonObject.deserializeFilterRangeOption(settings.min, settings.max)
        return StepperFilter(name, caption, value, settings)
    }


    private fun JsonObject.deserializeFilterOption(values: List<String>): FilterOption {
        val label = get("label").asString.capitalize()
        val value = get("value").asString
        val productCount = if (has("productCount")) get("productCount").asInt else null
        val selected = values.contains(value)
        return FilterOption(label, value, productCount, selected)
    }

    private fun JsonObject.deserializeFilterColorOption(values: List<String>): FilterColorOption {
        val id = get("fixedId").asString
        val label = get("label").asString.capitalize()
        val productCount = get("productCount").asInt
        val value = get("value").asString
        val selected = values.contains(value)
        return FilterColorOption(id, label, value, productCount, selected)
    }

    private fun JsonObject.deserializeFilterRangeOption(defaultMin: Int, defaultMax: Int): FilterRangeOption {
        val min = if (has("min")) get("min").asInt else defaultMin
        val max = if (has("max")) get("max").asInt else defaultMax
        return FilterRangeOption(min, max)
    }

    private fun JsonObject.deserializeFilterRangeSettings(): FilterRangeSettings {
        val min = get("min").asInt
        val max = get("max").asInt
        val stepSize = get("stepSize").asInt
        val unitLabel = get("unitLabel").asString
        return FilterRangeSettings(min, max, stepSize, unitLabel)
    }
}