package com.zeyad.rxredux.screens.user.detail;

import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.StateReducer;
import com.zeyad.rxredux.utils.Utils;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

import static com.zeyad.rxredux.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {

    private IDataService dataUseCase;

    @Override
    public void init(UserDetailState initialState,
                     Object... otherDependencies) {
        setState(initialState);
        if (dataUseCase == null) {
            dataUseCase = (IDataService) otherDependencies[0];
        }
    }

    @Override
    public StateReducer<UserDetailState> stateReducer() {
        return (newResult, event, currentStateBundle) -> UserDetailState.builder()
                .setRepos((List<Repository>) newResult).setUser(currentStateBundle.getUser())
                .setIsTwoPane(currentStateBundle.isTwoPane()).build();
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
        return event -> getRepositories(((GetReposEvent) event).getLogin());
    }

    public Flowable<List<Repository>> getRepositories(String userLogin) {
        return Utils.isNotEmpty(userLogin)
                ? dataUseCase
                .<Repository>queryDisk(
                        realm -> realm.where(Repository.class).equalTo("owner.login", userLogin))
                .flatMap(list -> Utils.isNotEmpty(list) ? Flowable.just(list)
                        : dataUseCase.<Repository>getList(new GetRequest.Builder(Repository.class, true)
                        .url(String.format(REPOSITORIES, userLogin)).build()))
                : Flowable.error(new IllegalArgumentException("User name can not be empty"));
    }
}
