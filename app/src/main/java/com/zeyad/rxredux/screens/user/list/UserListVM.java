package com.zeyad.rxredux.screens.user.list;

import static com.zeyad.rxredux.utils.Constants.URLS.USER;
import static com.zeyad.rxredux.utils.Constants.URLS.USERS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.SuccessStateAccumulator;
import com.zeyad.rxredux.screens.user.list.events.DeleteUsersEvent;
import com.zeyad.rxredux.screens.user.list.events.GetPaginatedUsersEvent;
import com.zeyad.rxredux.screens.user.list.events.SearchUsersEvent;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private IDataService dataUseCase;

    @Override
    public void init(SuccessStateAccumulator<UserListState> successStateAccumulator, UserListState initialState,
            Object... otherDependencies) {
        if (dataUseCase == null) {
            dataUseCase = (IDataService) otherDependencies[0];
        }
        setSuccessStateAccumulator(successStateAccumulator);
        setInitialState(initialState);
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
        return event -> {
            Flowable executable = Flowable.empty();
            if (event instanceof GetPaginatedUsersEvent) {
                executable = getUsers(((GetPaginatedUsersEvent) event).getLastId());
            } else if (event instanceof DeleteUsersEvent) {
                executable = deleteCollection(((DeleteUsersEvent) event).getSelectedItemsIds());
            } else if (event instanceof SearchUsersEvent) {
                executable = search(((SearchUsersEvent) event).getQuery());
            }
            return executable;
        };
    }

    public Flowable<User> getUser() {
        return dataUseCase
                .getObjectOffLineFirst(new GetRequest.Builder(User.class, true).url(String.format(USER, "Zeyad-37"))
                        .id("Zeyad-37", User.LOGIN, String.class).cache(User.LOGIN).build());
    }

    public Flowable<List<User>> getUsers(long lastId) {
        return lastId == 0
                ? dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                        .url(String.format(USERS, lastId)).cache(User.LOGIN).build())
                : dataUseCase.getList(
                        new GetRequest.Builder(User.class, true).url(String.format(USERS, lastId)).build());
    }

    public Flowable<List<User>> search(String query) {
        return dataUseCase.<User> queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(
                        dataUseCase
                                .<User> getObject(new GetRequest.Builder(User.class, false)
                                        .url(String.format(USER, query)).build())
                                .onErrorReturnItem(new User()).filter(user -> user.getId() != 0)
                                .map(user -> user != null ? Collections.singletonList(user)
                                        : Collections.emptyList()),
                        (BiFunction<List<User>, List<User>, List<User>>) (users, singleton) -> {
                            users.addAll(singleton);
                            return new ArrayList<>(new HashSet<>(users));
                        });
    }

    public Flowable<List<String>> deleteCollection(List<String> selectedItemsIds) {
        return dataUseCase.deleteCollectionByIds(new PostRequest.Builder(User.class, true).payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String.class).cache().build()).map(o -> selectedItemsIds);
    }
}
