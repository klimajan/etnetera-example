package com.example.android.ui.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.example.android.R
import com.example.android.data.model.RangeFilter
import com.example.android.data.model.StepperFilter
import com.example.android.ui.AppLifecycleObserver
import com.example.android.ui.MvRxSimpleFragment
import com.example.android.ui.category.CategoryState
import com.example.android.ui.category.CategoryViewModel
import com.example.android.utils.hide
import com.example.android.utils.show
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

class FilterStepperFragment : MvRxSimpleFragment() {
    private val viewModel: CategoryViewModel by activityViewModel()
    private val filterName by lazy { arguments?.getString("name") ?: "" }
    private lateinit var range: RangeSeekBar
    private lateinit var steppers: Group
    private lateinit var resetFilter: TextView
    private lateinit var from: TextView
    private lateinit var to: TextView
    private lateinit var fromMinus: ImageButton
    private lateinit var fromPlus: ImageButton
    private lateinit var toMinus: ImageButton
    private lateinit var toPlus: ImageButton
    override fun appLifecycleObserver(): AppLifecycleObserver = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_stepper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = withState(viewModel) { state -> state.filters?.firstOrNull { it.name == filterName }?.caption }
        view.findViewById<ImageView>(R.id.arrow)?.let {
            if (title == null) {
                it.hide()
            } else {
                it.setOnClickListener { activity?.onBackPressed() }
            }
        }

        view.findViewById<TextView>(R.id.title).text = title ?: "Filtry"
        fromMinus = view.findViewById<ImageButton>(R.id.from_minus).also { it.setOnClickListener { viewModel.updateStepperMin(filterName, false) } }
        fromPlus = view.findViewById<ImageButton>(R.id.from_plus).also { it.setOnClickListener { viewModel.updateStepperMin(filterName, true) } }
        toMinus = view.findViewById<ImageButton>(R.id.to_minus).also { it.setOnClickListener { viewModel.updateStepperMax(filterName, false) } }
        toPlus = view.findViewById<ImageButton>(R.id.to_plus).also { it.setOnClickListener { viewModel.updateStepperMax(filterName, true) } }
        from = view.findViewById(R.id.from)
        to = view.findViewById(R.id.to)
        resetFilter = view.findViewById<TextView>(R.id.reset_filter).also {
            it.text = getString(R.string.filter_reset_selected)
            it.setOnClickListener { viewModel.resetFilter(filterName) }
        }
        steppers = view.findViewById(R.id.steppers)
        range = view.findViewById<RangeSeekBar>(R.id.range).also {
            it.setOnRangeChangedListener(object : OnRangeChangedListener {
                override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
                override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
                override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                    if (isFromUser) viewModel.updateStepper(filterName, leftValue.toInt(), rightValue.toInt())
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeChanges()
    }

    @SuppressLint("SetTextI18n")
    private fun observeChanges() {
        viewModel.selectSubscribe(this, CategoryState::filters) { filters ->
            val filter = filters?.firstOrNull { it.name == filterName }
            if (filter?.isActiveFilter == true) resetFilter.show() else resetFilter.hide()

            when (filter) {
                is StepperFilter -> {
                    range.hide()
                    from.text = "${filter.value.min} ${filter.settings.unitLabel}"
                    to.text = "${filter.value.max} ${filter.settings.unitLabel}"
                    fromMinus.let { button ->
                        val disabled = filter.value.min == filter.settings.min
                        button.alpha = if (disabled) 0.3f else 1f
                        button.isEnabled = !disabled
                    }
                    fromPlus.let { button ->
                        val disabled = filter.value.min == filter.value.max
                        button.alpha = if (disabled) 0.3f else 1f
                        button.isEnabled = !disabled
                    }
                    toMinus.let { button ->
                        val disabled = filter.value.max == filter.value.min
                        button.alpha = if (disabled) 0.3f else 1f
                        button.isEnabled = !disabled
                    }
                    toPlus.let { button ->
                        val disabled = filter.value.max == filter.settings.max
                        button.alpha = if (disabled) 0.3f else 1f
                        button.isEnabled = !disabled
                    }
                }
                is RangeFilter -> {
                    steppers.hide()
                    from.text = "${filter.value.min} ${filter.settings.unitLabel}"
                    to.text = "${filter.value.max} ${filter.settings.unitLabel}"
                    range.let { bar ->
                        bar.setRange(filter.settings.min.toFloat(), filter.settings.max.toFloat(), 1f)
                        bar.setProgress(filter.value.min.toFloat(), filter.value.max.toFloat())
                    }
                }
            }
        }
    }
}