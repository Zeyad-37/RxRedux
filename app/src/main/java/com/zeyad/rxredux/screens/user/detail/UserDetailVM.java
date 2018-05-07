package com.zeyad.rxredux.screens.user.detail;

import com.zeyad.rxredux.core.BaseEvent;
import com.zeyad.rxredux.core.BaseViewModel;
import com.zeyad.rxredux.core.StateReducer;
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

    public UserDetailVM(IDataService dataService) {
        dataUseCase = dataService;
    }

    @Override
    public StateReducer<UserDetailState> stateReducer() {
        return (newResult, event, currentStateBundle) -> UserDetailState.CREATOR.builder()
                .setRepos((List<Repository>) newResult).setUser(currentStateBundle.getUser())
                .setIsTwoPane(currentStateBundle.isTwoPane()).build();
    }

    @Override
    public Function<BaseEvent<?>, Flowable<?>> mapEventsToActions() {
        return event -> getRepositories(((GetReposEvent) event).getPayLoad());
    }

    public Flowable<List<Repository>> getRepositories(String userLogin) {
        return dataUseCase.<Repository>queryDisk(realm ->
                realm.where(Repository.class).equalTo("owner.login", userLogin))
                .flatMap(list -> !list.isEmpty() ? Flowable.just(list)
                        : dataUseCase.<Repository>getList(new GetRequest.Builder(Repository.class, true)
                        .url(String.format(REPOSITORIES, userLogin)).build()));
    }
}
