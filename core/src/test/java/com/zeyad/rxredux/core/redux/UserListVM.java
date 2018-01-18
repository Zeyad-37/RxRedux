package com.zeyad.rxredux.core.redux;

import com.zeyad.gadapter.ItemInfo;
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

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private final IDataService dataUseCase;

    public UserListVM(IDataService dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    @Override
    public StateReducer<UserListState> stateReducer() {
        return (newResult, event, currentStateBundle) -> {
            List<User> users;
            if (currentStateBundle == null || currentStateBundle.getUsers() == null)
                users = new ArrayList<>();
            else users = Observable.fromIterable(currentStateBundle.getUsers())
                    .map(ItemInfo::<User>getData).toList().blockingGet();
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
                            .filter(user -> !((List) newResult).contains(user.getLogin()))
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
                action = getUsers(((GetPaginatedUsersEvent) event).getPayLoad());
            } else if (event instanceof DeleteUsersEvent) {
                action = deleteCollection(((DeleteUsersEvent) event).getPayLoad());
            } else if (event instanceof SearchUsersEvent) {
                action = search(((SearchUsersEvent) event).getPayLoad());
            }
            return action;
        };
    }

    private Flowable<List<User>> getUsers(long lastId) {
        return lastId == 0 ?
                dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                        .url(String.format("", lastId))
                        .cache(User.LOGIN)
                        .build())
                : dataUseCase.getList(new GetRequest.Builder(User.class, true)
                .url(String.format("", lastId)).build());
    }

    private Flowable<List<User>> search(String query) {
        return dataUseCase.<User>queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(dataUseCase.<User>getObject(new GetRequest.Builder(User.class, false)
                                .url(String.format("", query)).build())
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
