package com.zeyad.rxredux.core.redux;

import android.arch.lifecycle.ViewModel;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S> extends ViewModel {

    private SuccessStateAccumulator<S> successStateAccumulator;
    private S initialState;

    /**
     * @param successStateAccumulator a success State Accumulator.
     * @param initialState            Initial state to start with.
     */
    public abstract void init(SuccessStateAccumulator<S> successStateAccumulator,
                              S initialState, Object... otherDependencies);

    /**
     * A Transformer, given events returns UIModels by applying the redux pattern.
     *
     * @return {@link FlowableTransformer} the Redux pattern transformer.
     */
    FlowableTransformer<BaseEvent, UIModel<S>> uiModels() {
        return new FlowableTransformer<BaseEvent, UIModel<S>>() {
            @Override
            public Publisher<UIModel<S>> apply(@NonNull Flowable<BaseEvent> events) {
                return events.observeOn(Schedulers.io())
                        .flatMap(new Function<BaseEvent, Flowable<Result<?>>>() {
                            @Override
                            public Flowable<Result<?>> apply(@NonNull final BaseEvent event) throws Exception {
                                return Flowable.just(event)
                                        .flatMap(mapEventsToExecutables())
                                        .map(new Function<Object, Result<?>>() {
                                            @Override
                                            public Result apply(@NonNull Object result) throws Exception {
                                                return Result.successResult(new ResultBundle<>(event, result));
                                            }
                                        })
                                        .onErrorReturn(new Function<Throwable, Result<?>>() {
                                            @Override
                                            public Result apply(@NonNull Throwable error) throws Exception {
                                                return Result.errorResult(error);
                                            }
                                        })
                                        .startWith(Result.loadingResult());
                            }
                        })
                        .distinctUntilChanged(new BiPredicate<Result<?>, Result<?>>() {
                            @Override
                            public boolean test(@NonNull Result<?> objectResult, @NonNull Result<?> objectResult2) throws Exception {
                                return objectResult.getBundle().equals(objectResult2.getBundle()) ||
                                        (objectResult.isLoading() && objectResult2.isLoading());
                            }
                        })
                        .scan(UIModel.idleState(new ResultBundle<>("", initialState)),
                                new BiFunction<UIModel<S>, Result<?>, UIModel<S>>() {
                            @Override
                            public UIModel<S> apply(@NonNull UIModel<S> currentUIModel, @NonNull Result<?> result) throws Exception {
                                String event = result.getEvent();
                                S bundle = currentUIModel.getBundle();
                                if (result.isLoading()) {
                                    currentUIModel = UIModel.loadingState(new ResultBundle<>(event, bundle));
                                } else if (result.isSuccessful()) {
                                    currentUIModel = UIModel
                                            .successState(new ResultBundle<>(event, successStateAccumulator
                                                    .accumulateSuccessStates(result.getBundle(), event, bundle)));
                                } else {
                                    currentUIModel = UIModel.errorState(result.getError(),
                                            new ResultBundle<>(event, bundle));
                                }
                                return currentUIModel;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * A Function that given an event maps it to the correct executable logic.
     *
     * @return a {@link Function} the mapping function.
     */
    public abstract Function<BaseEvent, Flowable<?>> mapEventsToExecutables();

    public void setSuccessStateAccumulator(SuccessStateAccumulator<S> successStateAccumulator) {
        this.successStateAccumulator = successStateAccumulator;
    }

    public void setInitialState(S initialState) {
        this.initialState = initialState;
    }
}
