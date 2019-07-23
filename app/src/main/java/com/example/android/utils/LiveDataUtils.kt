package com.example.android.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData


fun <T1, T2, R> MediatorLiveData<R>.combineLatest(source1: LiveData<T1>, source2: LiveData<T2>, combineFunc: (T1, T2) -> R) {
    this.addSource(source1) {
        it?.let { value1 ->
            source2.value?.let { value2 ->
                this.value = combineFunc(value1, value2)
            }
        }
    }

    this.addSource(source2) {
        it?.let { value2 ->
            source1.value?.let { value1 ->
                this.value = combineFunc(value1, value2)
            }
        }
    }
}

fun <T1, T2, T3, R> MediatorLiveData<R>.combineLatest(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>, combineFunc: (T1, T2, T3) -> R) {
    this.addSource(source1) {
        it?.let { value1 ->
            source2.value?.let { value2 ->
                source3.value?.let { value3 ->
                    this.value = combineFunc(value1, value2, value3)
                }

            }
        }
    }

    this.addSource(source2) {
        it?.let { value2 ->
            source1.value?.let { value1 ->
                source3.value?.let { value3 ->
                    this.value = combineFunc(value1, value2, value3)
                }

            }
        }
    }

    this.addSource(source3) {
        it?.let { value3 ->
            source1.value?.let { value1 ->
                source2.value?.let { value2 ->
                    this.value = combineFunc(value1, value2, value3)
                }

            }
        }
    }
}