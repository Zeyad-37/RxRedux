package com.zeyad.rxredux.screens.user.list;

import android.support.annotation.NonNull;

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

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.zeyad.rxredux.utils.Constants.URLS.USER;
import static com.zeyad.rxredux.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    public static final int PAGE_SIZE = 6;
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
            List resultList = (List) newResult;
            List<User> users = currentStateBundle == null ? new ArrayList<>() : currentStateBundle.getUsers();
            List<User> searchList = new ArrayList<>();
            switch (event) {
                case "GetPaginatedUsersEvent":
                    users.addAll(resultList);
                    break;
                case "SearchUsersEvent":
                    searchList.clear();
                    searchList.addAll(resultList);
                    break;
                case "DeleteUsersEvent":
                    users = Observable.fromIterable(users).filter(user -> !resultList.contains((long) user.getId()))
                            .distinct().toList().blockingGet();
                    break;
                default:
                    break;
            }
            int lastId = users.get(users.size() - 1).getId();
            return UserListState.builder().users(users).searchList(searchList).lastId(lastId).build();
        };
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
        return lastId == 0 ?
                dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                        .url(String.format(USERS, lastId)).cache(User.LOGIN).build())
                : dataUseCase.getList(
                new GetRequest.Builder(User.class, true).url(String.format(USERS, lastId)).build());
    }

    public Flowable<List<User>> search(String query) {
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

    public Flowable<List<String>> deleteCollection(List<String> selectedItemsIds) {
        return dataUseCase.deleteCollectionByIds(new PostRequest.Builder(User.class, true).payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String.class).cache().build()).map(o -> selectedItemsIds);
    }

    @NonNull
    public GetPaginatedUsersEvent getGetPaginatedUsersEvent(int totalItemCount, int firstVisibleItemPosition,
                                                            int childCount, Integer integer) {
        if (integer == SCROLL_STATE_SETTLING) {
            if ((childCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE)
                return new GetPaginatedUsersEvent(getState().getLastId());
            else return new GetPaginatedUsersEvent(-1);
        } else {
            return new GetPaginatedUsersEvent(-1);
        }
    }
}
