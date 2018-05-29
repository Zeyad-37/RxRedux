package com.zeyad.rxredux.core

import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import android.support.v4.util.Pair
import com.zeyad.rxredux.core.Result.Companion.loadingResult
import com.zeyad.rxredux.core.Result.Companion.successResult
import com.zeyad.rxredux.core.UIModel.Companion.IDLE
import com.zeyad.rxredux.core.UIModel.Companion.errorState
import com.zeyad.rxredux.core.UIModel.Companion.loadingState
import com.zeyad.rxredux.core.UIModel.Companion.successState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * @author ZIaDo on 2/27/18.
 */
abstract class BaseViewModel<S : Parcelable>(private val eventsSubject: PublishSubject<BaseEvent<*>> =
                                                     PublishSubject.create()) : ViewModel() {

    abstract fun stateReducer(): StateReducer<S>

    abstract fun mapEventsToActions(): io.reactivex.functions.Function<BaseEvent<*>, Flowable<*>>

    fun processEvents(events: Observable<BaseEvent<*>>) = events.subscribe(eventsSubject)

    fun uiModels(initialState: S?): Flowable<UIModel<S>> =
            eventsSubject.toFlowable(BackpressureStrategy.BUFFER).compose<UIModel<S>>(uiModelsTransformer(initialState))

    private fun uiModelsTransformer(initialState: S?): FlowableTransformer<BaseEvent<*>, UIModel<S>> =
            FlowableTransformer { events ->
                events.observeOn(Schedulers.computation())
                        .flatMap { event ->
                            Flowable.just(event)
                                    .flatMap(mapEventsToActions())
                                    .compose<Result<*>>(mapActionsToResults(event.javaClass.simpleName))
                        }
                        .distinctUntilChanged { t1: Result<*>, t2: Result<*> -> t1 == t2 }
                        .scan(UIModel.idleState(Pair(IDLE, initialState)), reducer())
                        .replay(1)
                        .autoConnect()
                        .distinctUntilChanged { t1: UIModel<S>, t2: UIModel<S> -> t1 == t2 }
                        .observeOn(AndroidSchedulers.mainThread())
            }

    @NonNull
    private fun mapActionsToResults(eventName: String): FlowableTransformer<Any, Result<Any>> =
            FlowableTransformer {
                it.map { successResult(Pair(eventName, it)) }
                        .onErrorReturn { throwable -> Result.throwableResult(throwable) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(loadingResult())
                        .observeOn(Schedulers.computation())
            }

    @NonNull
    private fun reducer(): BiFunction<UIModel<S>, Result<*>, UIModel<S>> =
            BiFunction { currentUIModel, result ->
                when {
                    result.isLoading -> loadingState(Pair(result.getEvent()!!,
                            currentUIModel.getBundle()))
                    result.isSuccessful -> successState(Pair(result.getEvent()!!,
                            stateReducer().reduce(result.getBundle(), result.getEvent(),
                                    currentUIModel.getBundle())))
                    else -> errorState(result.throwable, Pair(result.getEvent()!!,
                            currentUIModel.getBundle()))
                }
            }
}
