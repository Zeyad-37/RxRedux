package com.zeyad.rxredux.core.redux;

import com.zeyad.rxredux.core.BaseEvent;
import com.zeyad.rxredux.core.ImmediateSchedulersRule;
import com.zeyad.rxredux.core.PModel;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseViewModelTest {

    @Rule
    public final ImmediateSchedulersRule testSchedulerRule = new ImmediateSchedulersRule();

    private IDataService mockDataUseCase;
    private UserListVM viewModel;

    @Before
    public void setUp() {
        mockDataUseCase = mock(IDataService.class);
        viewModel = new UserListVM(mockDataUseCase);
    }

    @Test
    public void uiModels() {
        List<User> userList1;
        User user = new User();
        user.setLogin("testUser1");
        user.setId(1);
        userList1 = new ArrayList<>();
        userList1.add(user);

        Flowable<List<User>> getListOfflineFirstResult = Flowable.just(userList1);
        List<User> userList2;
        User user2 = new User();
        user.setLogin("testUser2");
        user.setId(2);
        userList2 = new ArrayList<>();
        userList2.add(user2);
        Flowable<List<User>> getListResult = Flowable.just(userList2);

        Flowable<List<String>> deleteResult = Flowable.just(Flowable.fromArray("testUser1", "testUser2").toList
                ().blockingGet());

        when(mockDataUseCase.<User>getListOffLineFirst((GetRequest) any())).thenReturn(getListOfflineFirstResult);
        when(mockDataUseCase.<User>getList((GetRequest) any())).thenReturn(getListResult);
        when(mockDataUseCase.<User>queryDisk((RealmQueryProvider) any())).thenReturn(getListOfflineFirstResult);
        when(mockDataUseCase.<User>getObject((GetRequest) any())).thenReturn(Flowable.just(user));

        when(mockDataUseCase.<List<String>>deleteCollectionByIds((PostRequest) any())).thenReturn(deleteResult);

        TestSubscriber<PModel<UserListState>> testSubscriber =
                viewModel.store(Observable.<BaseEvent<?>>just(new GetPaginatedUsersEvent(1)),
                        UserListState.builder()
                                .lastId(0)
                                .build())
                        .test();

//        viewModel.store(Observable.fromArray(new GetPaginatedUsersEvent(0),
//                new GetPaginatedUsersEvent(1),
//                new DeleteUsersEvent(Collections.singletonList("1"))));
//                ,
//                new SearchUsersEvent("1")));

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();

//        testSubscriber.assertValueAt(0,
//                userListStateUIModel -> userListStateUIModel.getEvent().equals("idle"));
//
//        testSubscriber.assertValueAt(1, Result::isLoading);
//
//        testSubscriber.assertValueAt(2, tasksViewState -> !tasksViewState.isLoading());
//        testSubscriber.assertValueAt(2, Result::isSuccessful);
//        testSubscriber.assertValueAt(2,
//                tasksViewState -> tasksViewState.getBundle().getUsers().size() == 1);
//
//        testSubscriber.assertValueAt(3, Result::isLoading);
//
//        testSubscriber.assertValueAt(4, tasksViewState -> !tasksViewState.isLoading());
//        testSubscriber.assertValueAt(4, Result::isSuccessful);
//        testSubscriber.assertValueAt(4,
//                tasksViewState -> tasksViewState.getBundle().getUsers().size() == 2);

        verify(mockDataUseCase, times(1)).getListOffLineFirst(any(GetRequest.class));
        verify(mockDataUseCase, times(1)).getList(any(GetRequest.class));
    }
}