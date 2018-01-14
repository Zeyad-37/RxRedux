package com.zeyad.rxredux.core.redux;

import com.zeyad.gadapter.ItemInfo;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private IDataService dataUseCase;

    @Override
    public void init(Object... dependencies) {
        if (dataUseCase == null) {
            dataUseCase = (IDataService) dependencies[0];
        }
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
        return event -> getUsers(((GetPaginatedUsersEvent) event).getPayLoad());
    }

    private Flowable<List<User>> getUsers(long lastId) {
        return lastId == 0 ? dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                .url(String.format("/users", lastId))
                .cache(User.LOGIN)
                .build()) : dataUseCase.getList(new GetRequest.Builder(User.class, true)
                .url(String.format("/users", lastId)).build());
    }
}
