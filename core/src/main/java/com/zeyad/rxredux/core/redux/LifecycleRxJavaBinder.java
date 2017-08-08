package com.zeyad.rxredux.core.redux;

/**
 * @author by Zeyad.
 */
class LifecycleRxJavaBinder {
    //    @NonNull
    //    public static <T> FlowableTransformer<T, T> applyFlowable(@NonNull final LifecycleOwner lifecycleOwner) {
    //        return new FlowableTransformer<T, T>() {
    //            @Override
    //            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
    //                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner,
    //                        LiveDataReactiveStreams.fromPublisher(upstream)));
    //            }
    //        };
    //    }
    //
    //    @NonNull
    //    public static <T> ObservableTransformer<T, T> applyObservable(@NonNull final LifecycleOwner lifecycleOwner,
    //            @NonNull final BackpressureStrategy strategy) {
    //        return new ObservableTransformer<T, T>() {
    //            @Override
    //            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
    //                return Flowable
    //                        .fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner,
    //                                LiveDataReactiveStreams.fromPublisher(upstream.toFlowable(strategy))))
    //                        .toObservable();
    //            }
    //        };
    //    }
    //
    //    @NonNull
    //    public static <T> ObservableTransformer<T, T> applyObservable(@NonNull final LifecycleOwner lifecycleOwner) {
    //        return new ObservableTransformer<T, T>() {
    //            @Override
    //            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
    //                return Flowable
    //                        .fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner,
    //                                LiveDataReactiveStreams
    //                                        .fromPublisher(upstream.toFlowable(BackpressureStrategy.BUFFER))))
    //                        .toObservable();
    //            }
    //        };
    //    }
    //
    //    @NonNull
    //    public static <T> SingleTransformer<T, T> applySingle(@NonNull final LifecycleOwner lifecycleOwner) {
    //        return new SingleTransformer<T, T>() {
    //            @Override
    //            public SingleSource<T> apply(@NonNull Single<T> upstream) {
    //                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner,
    //                        LiveDataReactiveStreams.fromPublisher(upstream.toFlowable())))
    //                        .singleOrError();
    //            }
    //        };
    //    }
    //
    //    @NonNull
    //    public static <T> MaybeTransformer<T, T> applyMaybe(@NonNull final LifecycleOwner lifecycleOwner) {
    //        return new MaybeTransformer<T, T>() {
    //            @Override
    //            public MaybeSource<T> apply(@NonNull Maybe<T> upstream) {
    //                return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner,
    //                        LiveDataReactiveStreams.fromPublisher(upstream.toFlowable())))
    //                        .firstElement();
    //            }
    //        };
    //    }
}
