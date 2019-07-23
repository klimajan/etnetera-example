package com.example.android.utils

import androidx.databinding.ObservableField
import com.example.android.data.remote.RestHttpException
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

fun Completable.composeSchedulers(): Completable = this
        .compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }

fun <T> Maybe<T>.composeSchedulers(): Maybe<T> = this
        .compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }

fun <T> Single<T>.composeSchedulers(): Single<T> = this
        .compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }

fun <T> Flowable<T>.composeSchedulers(): Flowable<T> = this
        .compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true) }

fun <T> Observable<T>.composeSchedulers(): Observable<T> = this
        .compose { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true) }


fun <T : Response<*>> Maybe<T>.applyErrors(): Maybe<T> = this
        .flatMap {
            when (it.isSuccessful) {
                true -> Maybe.just(it)
                else -> Maybe.error(RestHttpException(it))
            }
        }

fun <T : Response<*>> Single<T>.applyErrors(): Single<T> = this
        .flatMap {
            when (it.isSuccessful) {
                true -> Single.just(it)
                else -> Single.error(RestHttpException(it))
            }
        }

fun <T : Response<*>> Flowable<T>.applyErrors(): Flowable<T> = this
        .flatMap {
            when (it.isSuccessful) {
                true -> Flowable.just(it)
                else -> Flowable.error(RestHttpException(it))
            }
        }

fun <T> ObservableField<T>.toObservable(): Observable<T> {
    return Observable.create {
        val callback = object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(field: androidx.databinding.Observable, propertyId: Int) {
                get()?.let { f -> it.onNext(f) }
            }
        }
        addOnPropertyChangedCallback(callback)
        it.setDisposable(Disposables.fromAction { removeOnPropertyChangedCallback(callback) })
    }
}