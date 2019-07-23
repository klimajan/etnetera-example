package com.example.android.data.model

import com.google.gson.Gson
import java.util.*

sealed class Filter {
    abstract val name: String
    abstract val caption: String
    abstract val filteredValue: String?
    abstract val filterParams: String?
    abstract val isActiveFilter: Boolean
    abstract fun resetFilter(): Filter
    abstract fun setFilteredValue(value: Any): Filter
}

data class Filters(val filters: List<Filter>)

enum class FilterType {
    OPTION_LIST, TICK_LIST, TICK_LIST_SEARCH, RANGE, COLOR, STEPPER
}

data class FilterOption(val label: String, val value: String, val productCount: Int?, val selected: Boolean)

data class FilterColorOption(val id: String, val label: String, val value: String, val productCount: Int, val selected: Boolean)

data class FilterRangeOption(val min: Int, val max: Int)

data class FilterRangeSettings(val min: Int, val max: Int, val stepSize: Int, val unitLabel: String)

data class Sorting(val value: String, val caption: String, val selected: Boolean = false)

/**
 * ALL TYPES
 */
data class OptionListFilter(override val name: String, override val caption: String, val options: List<FilterOption>) : Filter() {
    override val filteredValue: String?
        get() {
            return options.firstOrNull { it.selected }?.label
        }

    override val filterParams: String?
        get() {
            val key = name.quotation()
            val value = options.firstOrNull { it.selected && it.value != "all"}?.value?.quotation()
            return if (value == null) null else "$key:$value"
        }

    override val isActiveFilter: Boolean
        get() = options.any { it.selected && it.value != "all" }

    override fun setFilteredValue(value: Any): Filter {
        return copy(options = options.map { option -> if (option.value == value) option.copy(selected = true) else option.copy(selected = false) })
    }

    override fun resetFilter(): Filter {
        return copy(options = options.mapIndexed { index, option -> if (index == 0) option.copy(selected = true) else option.copy(selected = false) })
    }
}

data class TickListFilter(override val name: String, override val caption: String, val options: List<FilterOption>) : Filter() {
    override val filteredValue: String?
        get() {
            val selectedValues = options.filter { it.selected }
            return if (selectedValues.isEmpty()) null else selectedValues.joinToString(", ") { it.label }
        }

    override val filterParams: String?
        get() {
            val option = options.filter { it.selected }
            val key = name.quotation()
            val value = option.map { it.value.quotation() }.toString()
            return if (option.isEmpty()) null else "$key:$value"
        }

    override val isActiveFilter: Boolean
        get() = options.any { it.selected }

    override fun setFilteredValue(value: Any): Filter {
        return copy(options = options.map { option -> if (option.value == value) option.copy(selected = !option.selected) else option })
    }

    override fun resetFilter(): Filter {
        return copy(options = options.map { option -> if (option.selected) option.copy(selected = false) else option })
    }
}

data class ColorFilter(override val name: String, override val caption: String, val options: List<FilterColorOption>) : Filter() {
    override val filteredValue: String?
        get() {
            val selectedValues = options.filter { it.selected }
            return if (selectedValues.isEmpty()) null else selectedValues.joinToString(", ") { it.label }
        }

    override val filterParams: String?
        get() {
            val option = options.filter { it.selected }
            val key = name.quotation()
            val value = option.map { it.value.quotation() }.toString()
            return if (option.isEmpty()) null else "$key:$value"
        }

    override val isActiveFilter: Boolean
        get() = options.any { it.selected }

    override fun setFilteredValue(value: Any): Filter {
        return copy(options = options.map { option -> if (option.value == value) option.copy(selected = !option.selected) else option })
    }

    override fun resetFilter(): Filter {
        return copy(options = options.map { option -> if (option.selected) option.copy(selected = false) else option })
    }

}

data class RangeFilter(override val name: String, override val caption: String, val value: FilterRangeOption, val settings: FilterRangeSettings) : Filter() {
    override val filteredValue: String?
        get() {
            return if (!isActiveFilter) null
            else if (value.min == settings.min) "Do ${value.max.thousands()} ${settings.unitLabel}"
            else if (value.max == settings.max) "Od ${value.min.thousands()} ${settings.unitLabel}"
            else "Od ${value.min.thousands()} ${settings.unitLabel} do ${value.max.thousands()} ${settings.unitLabel}"
        }

    override val filterParams: String?
        get() {
            if (!isActiveFilter) return null
            val key = name.quotation()
            val value = Gson().toJson(value)
            return "$key:$value"
        }

    override val isActiveFilter: Boolean
        get() = value.min != settings.min || value.max != settings.max

    override fun setFilteredValue(value: Any): Filter {
        return if (value is FilterRangeOption) copy(value = value) else this
    }

    override fun resetFilter(): Filter {
        return copy(value = value.copy(min = settings.min, max = settings.max))
    }
}

data class StepperFilter(override val name: String, override val caption: String, val value: FilterRangeOption, val settings: FilterRangeSettings) : Filter() {
    override val filteredValue: String?
        get() {
            return if (!isActiveFilter) null
            else if (value.min == settings.min) "Do ${value.max} ${settings.unitLabel}"
            else if (value.max == settings.max) "Od ${value.min} ${settings.unitLabel}"
            else "Od ${value.min} ${settings.unitLabel} do ${value.max} ${settings.unitLabel}"
        }

    override val filterParams: String?
        get() {
            if (!isActiveFilter) return null
            val key = name.quotation()
            val value = Gson().toJson(value)
            return "$key:$value"
        }

    override val isActiveFilter: Boolean
        get() = value.min != settings.min || value.max != settings.max

    override fun setFilteredValue(value: Any): Filter {
        return if (value is FilterRangeOption) copy(value = value) else this
    }

    override fun resetFilter(): Filter {
        return copy(value = value.copy(min = settings.min, max = settings.max))
    }
}

private fun String.quotation(): String {
    return "\"$this\""
}

private fun Int.thousands(): String {
    return String.format(Locale("cs"), "%,d", this)
}