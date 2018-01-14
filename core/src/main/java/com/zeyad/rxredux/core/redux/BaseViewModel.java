package com.zeyad.rxredux.core.redux;

import android.arch.lifecycle.ViewModel;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static android.support.v4.util.Pair.create;
import static com.zeyad.rxredux.core.redux.Result.loadingResult;
import static com.zeyad.rxredux.core.redux.Result.successResult;
import static com.zeyad.rxredux.core.redux.UIModel.IDLE;
import static com.zeyad.rxredux.core.redux.UIModel.errorState;
import static com.zeyad.rxredux.core.redux.UIModel.idleState;
import static com.zeyad.rxredux.core.redux.UIModel.loadingState;
import static com.zeyad.rxredux.core.redux.UIModel.successState;

/*** @author Zeyad. */
public abstract class BaseViewModel<S> extends ViewModel {

    private PublishSubject<BaseEvent> eventsSubject = PublishSubject.create();

    /**
     * A different way to initialize an instance without a constructor
     */
    public abstract void init(Object... dependencies);

    /**
     * Provide a success state reducer
     *
     * @return {@link StateReducer}
     */
    public abstract StateReducer<S> stateReducer();

    /**
     * A Function that given an event maps it to the correct executable logic.
     *
     * @return a {@link Function} the mapping function.
     */
    @NonNull
    protected abstract Function<BaseEvent, Flowable<?>> mapEventsToActions();

    /**
     * Pass in the stream of events to start the dialog.
     *
     * @param events {@link Observable} stream of user events.
     */
    public void processEvents(Observable<BaseEvent> events) {
        events.subscribe(eventsSubject);
    }

    /**
     * A Transformer, given eventObservable returns UIModels by applying the redux pattern.
     *
     * @return {@link FlowableTransformer} the Redux pattern transformer.
     */
    @NonNull
    public Flowable<UIModel<S>> uiModels(S initialState) {
        return eventsSubject.toFlowable(BackpressureStrategy.BUFFER)
                .compose(uiModelsTransformer(initialState));
    }

    @NonNull
    private FlowableTransformer<BaseEvent, UIModel<S>> uiModelsTransformer(S initialState) {
        return events -> events.observeOn(Schedulers.computation())
                .flatMap(event -> Flowable.just(event)
                        .flatMap(mapEventsToActions())
                        .compose(mapActionsToResults(event.getClass().getSimpleName())))
                .distinctUntilChanged(Result::equals)
                .scan(idleState(create(IDLE, initialState)), reducer())
                .replay(1)
                .autoConnect(0)
                .distinctUntilChanged(UIModel::equals)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private BiFunction<UIModel<S>, Result<?>, UIModel<S>> reducer() {
        return (currentUIModel, result) -> {
            String event = result.getEvent();
            S bundle = currentUIModel.getBundle();
            if (result.isLoading()) {
                currentUIModel = loadingState(create(event, bundle));
            } else if (result.isSuccessful()) {
                currentUIModel = successState(create(event,
                        stateReducer().reduce(result.getBundle(), event, bundle)));
            } else {
                currentUIModel = errorState(result.getThrowable(), create(event, bundle));
            }
            return currentUIModel;
        };
    }

    @NonNull
    private FlowableTransformer<Object, Result<?>> mapActionsToResults(String eventName) {
        return upstream -> upstream
                .map((Function<Object, Result<?>>) result -> successResult(create(eventName, result)))
                .onErrorReturn(Result::throwableResult)
                .startWith(loadingResult());
    }
}
