package com.zeyad.rxredux.core.redux;

import org.reactivestreams.Publisher;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.annotations.NonNull;

/**
 * @author by Zeyad.
 */
class LifecycleRxJavaBinder {
    @android.support.annotation.NonNull
    public static <T> FlowableTransformer<T, T> applyFlowable(@NonNull final LifecycleOwner lifecycleOwner) {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(@android.support.annotation.NonNull @NonNull Flowable<T> upstream) {
                LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(upstream);
                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, liveData));
            }
        };
    }

    @android.support.annotation.NonNull
    public static <T> ObservableTransformer<T, T> applyObservable(@NonNull final LifecycleOwner lifecycleOwner,
            @android.support.annotation.NonNull final BackpressureStrategy strategy) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@android.support.annotation.NonNull @NonNull Observable<T> upstream) {
                LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(upstream.toFlowable(strategy));
                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, liveData))
                        .toObservable();
            }
        };
    }

    @android.support.annotation.NonNull
    public static <T> ObservableTransformer<T, T> applyObservable(@NonNull final LifecycleOwner lifecycleOwner) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@android.support.annotation.NonNull @NonNull Observable<T> upstream) {
                LiveData<T> liveData = LiveDataReactiveStreams
                        .fromPublisher(upstream.toFlowable(BackpressureStrategy.BUFFER));
                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, liveData))
                        .toObservable();
            }
        };
    }

    @android.support.annotation.NonNull
    public static <T> SingleTransformer<T, T> applySingle(@NonNull final LifecycleOwner lifecycleOwner) {
        return new SingleTransformer<T, T>() {
            @Override
            public SingleSource<T> apply(@android.support.annotation.NonNull @NonNull Single<T> upstream) {
                LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(upstream.toFlowable());
                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, liveData))
                        .singleOrError();
            }
        };
    }

    @android.support.annotation.NonNull
    public static <T> MaybeTransformer<T, T> applyMaybe(@NonNull final LifecycleOwner lifecycleOwner) {
        return new MaybeTransformer<T, T>() {
            @Override
            public MaybeSource<T> apply(@android.support.annotation.NonNull @NonNull Maybe<T> upstream) {
                LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(upstream.toFlowable());
                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, liveData))
                        .firstElement();
            }
        };
    }
}
