package com.zeyad.rxredux.screens.user.list;

import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.StateReducer;
import com.zeyad.rxredux.screens.user.User;
import com.zeyad.rxredux.screens.user.list.events.DeleteUsersEvent;
import com.zeyad.rxredux.screens.user.list.events.GetPaginatedUsersEvent;
import com.zeyad.rxredux.screens.user.list.events.SearchUsersEvent;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static com.zeyad.rxredux.utils.Constants.URLS.USER;
import static com.zeyad.rxredux.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private IDataService dataUseCase;

    @Override
    public void init(UserListState initialState, Object... otherDependencies) {
        if (dataUseCase == null) {
            dataUseCase = (IDataService) otherDependencies[0];
        }
        setState(initialState);
    }

    @Override
    public StateReducer<UserListState> stateReducer() {
        return (newResult, event, currentStateBundle) -> {
            List<User> users = currentStateBundle == null ? new ArrayList<>() :
                    currentStateBundle.getUsers();
            List<User> searchList = new ArrayList<>();
            switch (event) {
                case "GetPaginatedUsersEvent":
                    users.addAll((List) newResult);
                    break;
                case "SearchUsersEvent":
                    searchList.addAll((List) newResult);
                    break;
                case "DeleteUsersEvent":
                    users = Observable.fromIterable(users)
                            .filter(user -> !((List) newResult).contains((long) user.getId()))
                            .distinct().toList().blockingGet();
                    break;
                default:
                    break;
            }
            return UserListState.builder()
                    .users(users)
                    .searchList(searchList)
                    .lastId(users.get(users.size() - 1).getId())
                    .build();
        };
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToActions() {
        return event -> {
            Flowable action = Flowable.empty();
            if (event instanceof GetPaginatedUsersEvent) {
                action = getUsers(((GetPaginatedUsersEvent) event).getLastId());
            } else if (event instanceof DeleteUsersEvent) {
                action = deleteCollection(((DeleteUsersEvent) event).getSelectedItemsIds());
            } else if (event instanceof SearchUsersEvent) {
                action = search(((SearchUsersEvent) event).getQuery());
            }
            return action;
        };
    }

    private Flowable<List<User>> getUsers(long lastId) {
        return lastId == 0 ?
                dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                        .url(String.format(USERS, lastId))
                        .cache(User.LOGIN)
                        .build())
                : dataUseCase.getList(new GetRequest.Builder(User.class, true)
                .url(String.format(USERS, lastId)).build());
    }

    private Flowable<List<User>> search(String query) {
        return dataUseCase.<User>queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(dataUseCase.<User>getObject(new GetRequest.Builder(User.class, false)
                                .url(String.format(USER, query)).build())
                                .onErrorReturnItem(new User()).filter(user -> user.getId() != 0)
                                .map(user -> user != null ? Collections.singletonList(user)
                                        : Collections.emptyList()),
                        (BiFunction<List<User>, List<User>, List<User>>) (users, singleton) -> {
                            users.addAll(singleton);
                            return new ArrayList<>(new HashSet<>(users));
                        });
    }

    private Flowable<List<String>> deleteCollection(List<String> selectedItemsIds) {
        return dataUseCase.deleteCollectionByIds(new PostRequest.Builder(User.class, true).payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String.class).cache().build()).map(o -> selectedItemsIds);
    }
}
