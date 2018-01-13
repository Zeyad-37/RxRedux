package com.zeyad.rxredux.core.redux;

import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.zeyad.rxredux.core.redux.Result.loadingResult;
import static com.zeyad.rxredux.core.redux.Result.successResult;
import static com.zeyad.rxredux.core.redux.UIModel.IDLE;
import static com.zeyad.rxredux.core.redux.UIModel.errorState;
import static com.zeyad.rxredux.core.redux.UIModel.idleState;
import static com.zeyad.rxredux.core.redux.UIModel.loadingState;
import static com.zeyad.rxredux.core.redux.UIModel.successState;

/*** @author Zeyad. */
public abstract class BaseViewModel<S> extends ViewModel {

    private S state;

    /**
     * A different way to initialize an instance without a constructor
     *
     * @param initialState Initial state to start with.
     */
    // TODO: 12/11/17 Use DI!
    public abstract void init(@Nullable S initialState, Object... otherDependencies);

    public abstract StateReducer<S> stateReducer();

    /**
     * A Transformer, given events returns UIModels by applying the redux pattern.
     *
     * @return {@link FlowableTransformer} the Redux pattern transformer.
     */
    @NonNull
    public FlowableTransformer<BaseEvent, UIModel<S>> uiModels() {
        return events -> events.observeOn(Schedulers.computation())
                .flatMap(event -> Flowable.just(event)
                        .flatMap(mapEventsToActions())
                        .map((Function<Object, Result<?>>) result ->
                                successResult(Pair.create(event.getClass().getSimpleName(), result)))
                        .onErrorReturn(Result::throwableResult)
                        .startWith(loadingResult()))
                .distinctUntilChanged(Result::equals)
                .scan(idleState(Pair.create(IDLE, state)),
                        (currentUIModel, result) -> {
                            String event = result.getEvent();
                            S bundle = currentUIModel.getBundle();
                            if (result.isLoading()) {
                                currentUIModel = loadingState(Pair.create(event, bundle));
                            } else if (result.isSuccessful()) {
                                currentUIModel = successState(Pair.create(event,
                                        stateReducer().reduce(result.getBundle(), event, bundle)));
                            } else {
                                currentUIModel = errorState(result.getThrowable(), Pair.create(event, bundle));
                            }
                            return currentUIModel;
                        })
                .distinctUntilChanged(UIModel::equals)
                .doOnNext(suiModel -> state = suiModel.getBundle())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * A Function that given an event maps it to the correct executable logic.
     *
     * @return a {@link Function} the mapping function.
     */
    @NonNull
    public abstract Function<BaseEvent, Flowable<?>> mapEventsToActions();

    public S getState() {
        return state;
    }

    public void setState(S state) {
        if (this.state == null || !this.state.equals(state)) {
            this.state = state;
        }
    }
}
