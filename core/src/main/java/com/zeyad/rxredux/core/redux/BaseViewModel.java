package com.zeyad.rxredux.core.redux;

import static com.zeyad.rxredux.core.redux.Result.loadingResult;
import static com.zeyad.rxredux.core.redux.Result.successResult;
import static com.zeyad.rxredux.core.redux.UIModel.IDLE;
import static com.zeyad.rxredux.core.redux.UIModel.errorState;
import static com.zeyad.rxredux.core.redux.UIModel.idleState;
import static com.zeyad.rxredux.core.redux.UIModel.loadingState;
import static com.zeyad.rxredux.core.redux.UIModel.successState;

import android.arch.lifecycle.ViewModel;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/*** @author Zeyad. */
public abstract class BaseViewModel<S> extends ViewModel {

    private SuccessStateAccumulator<S> successStateAccumulator;
    private S initialState;

    /**
     * A different way to initialize an instance without a constructor
     *
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
    @NonNull
    public FlowableTransformer<BaseEvent, UIModel<S>> uiModels() {
        return new FlowableTransformer<BaseEvent, UIModel<S>>() {
            @Override
            public Flowable<UIModel<S>> apply(@NonNull Flowable<BaseEvent> events) {
                return events.observeOn(Schedulers.io())
                        .flatMap(new Function<BaseEvent, Flowable<Result<?>>>() {
                            @Override
                            public Flowable<Result<?>> apply(@NonNull final BaseEvent event) throws Exception {
                                return Flowable.just(event)
                                        .flatMap(mapEventsToExecutables())
                                        .map((Function<Object, Result<?>>) result -> successResult(
                                                new ResultBundle<>(event, result)))
                                        .onErrorReturn(Result::errorResult)
                                        .startWith(loadingResult());
                            }
                        })
                        .distinctUntilChanged((objectResult,
                                objectResult2) -> objectResult.getBundle().equals(objectResult2.getBundle())
                                        || (objectResult.isLoading() && objectResult2.isLoading()))
                        .scan(idleState(new ResultBundle<>(IDLE, initialState)),
                                (currentUIModel, result) -> {
                                    String event = result.getEvent();
                                    S bundle = currentUIModel.getBundle();
                                    if (result.isLoading()) {
                                        currentUIModel = loadingState(new ResultBundle<>(event, bundle));
                                    } else if (result.isSuccessful()) {
                                        currentUIModel = successState(new ResultBundle<>(event,
                                                successStateAccumulator.accumulateSuccessStates(result.getBundle(),
                                                        event, bundle)));
                                    } else {
                                        currentUIModel = errorState(result.getError(),
                                                new ResultBundle<>(event, bundle));
                            }
                                    return currentUIModel;
                        })
                        .doOnNext(suiModel -> initialState = suiModel.getBundle())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * A Function that given an event maps it to the correct executable logic.
     *
     * @return a {@link Function} the mapping function.
     */
    @NonNull
    public abstract Function<BaseEvent, Flowable<?>> mapEventsToExecutables();

    public void setSuccessStateAccumulator(SuccessStateAccumulator<S> successStateAccumulator) {
        if (this.successStateAccumulator == null) {
            this.successStateAccumulator = successStateAccumulator;
        }
    }

    public void setInitialState(S initialState) {
        if (this.initialState == null || !this.initialState.equals(initialState)) {
            this.initialState = initialState;
        }
    }
}
