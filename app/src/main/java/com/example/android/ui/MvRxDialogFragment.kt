package com.example.android.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewModelStore
import com.example.android.R
import dagger.android.support.AndroidSupportInjection
import java.util.*

abstract class MvRxDialogFragment : DialogFragment(), MvRxView {
    override val mvrxViewModelStore by lazy { MvRxViewModelStore(viewModelStore) }
    final override val mvrxViewId: String by lazy { mvrxPersistedViewId }

    private lateinit var mvrxPersistedViewId: String
    protected lateinit var recyclerView: EpoxyRecyclerView
    private val epoxyController by lazy { epoxyController() }

    abstract fun epoxyController(): EpoxyController
    abstract fun appLifecycleObserver(): AppLifecycleObserver
    abstract fun title(): String

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mvrxViewModelStore.restoreViewModels(this, savedInstanceState)
        mvrxPersistedViewId = savedInstanceState?.getString(PERSISTED_VIEW_ID_KEY) ?: this::class.java.simpleName + "_" + UUID.randomUUID().toString()
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(appLifecycleObserver())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_base_mvrx, container, false).apply {
            recyclerView = findViewById<EpoxyRecyclerView>(R.id.recycler_view).apply {
                setController(epoxyController)
            }
            findViewById<TextView>(R.id.dialog_title).text = title()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvrxViewModelStore.saveViewModels(outState)
        outState.putString(PERSISTED_VIEW_ID_KEY, mvrxViewId)
    }

    override fun onStart() {
        super.onStart()
        // This ensures that invalidate() is called for static screens that don't
        // subscribe to a ViewModel.
        postInvalidate()
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window!!.attributes
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog?.window!!.attributes = params as android.view.WindowManager.LayoutParams
    }
}

private const val PERSISTED_VIEW_ID_KEY = "mvrx:persisted_view_id"