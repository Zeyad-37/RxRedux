package com.zeyad.rxredux.screens.user.detail;

import static com.zeyad.rxredux.utils.Constants.URLS.REPOSITORIES;

import java.util.List;

import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.SuccessStateAccumulator;
import com.zeyad.rxredux.utils.Utils;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {

    private IDataService dataUseCase;

    @Override
    public void init(SuccessStateAccumulator<UserDetailState> successStateAccumulator, UserDetailState initialState,
            Object... otherDependencies) {
        setSuccessStateAccumulator(successStateAccumulator);
        setInitialState(initialState);
        if (dataUseCase == null) {
            dataUseCase = (IDataService) otherDependencies[0];
        }
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
        return event -> getRepositories(((GetReposEvent) event).getLogin());
    }

    public Flowable<List<Repository>> getRepositories(String userLogin) {
        return Utils.isNotEmpty(userLogin)
                ? dataUseCase
                        .<Repository> queryDisk(
                                realm -> realm.where(Repository.class).equalTo("owner.login", userLogin))
                        .flatMap(list -> Utils.isNotEmpty(list) ? Flowable.just(list)
                                : dataUseCase.<Repository> getList(new GetRequest.Builder(Repository.class, true)
                                        .url(String.format(REPOSITORIES, userLogin)).build()))
                : Flowable.error(new IllegalArgumentException("User name can not be empty"));
    }
}
