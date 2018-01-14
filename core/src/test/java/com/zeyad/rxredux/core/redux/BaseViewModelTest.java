package com.zeyad.rxredux.core.redux;

import com.zeyad.rxredux.core.ImmediateSchedulersRule;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.requests.GetRequest;

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

/**
 * @author ZIaDo on 1/14/18.
 */
public class BaseViewModelTest {

    @Rule
    public final ImmediateSchedulersRule testSchedulerRule = new ImmediateSchedulersRule();

    private IDataService mockDataUseCase;
    private UserListVM viewModel;

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataService.class);
        viewModel = new UserListVM();
        viewModel.init(mockDataUseCase);
    }

    @Test
    public void uiModels() throws Exception {
        List<User> userList;
        User user = new User();
        user.setLogin("testUser");
        user.setId(1);
        userList = new ArrayList<>();
        userList.add(user);

        Flowable<List<User>> observableUserRealm = Flowable.just(userList);

        when(mockDataUseCase.<User>getListOffLineFirst(any())).thenReturn(observableUserRealm);

        TestSubscriber<UIModel<UserListState>> testSubscriber =
                viewModel.uiModels(UserListState.builder().lastId(0).build()).test();

        viewModel.processEvents(Observable.just(new GetPaginatedUsersEvent(0)));

        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertValueAt(0,
                userListStateUIModel -> userListStateUIModel.getEvent().equals("idle"));

        testSubscriber.assertValueAt(1, Result::isLoading);
        testSubscriber.assertNoErrors();
        // Then progress indicator state is canceled and all tasks are emitted
        testSubscriber.assertValueAt(2, tasksViewState -> !tasksViewState.isLoading());
        testSubscriber.assertValueAt(2, Result::isSuccessful);
        testSubscriber.assertValueAt(2,
                tasksViewState -> tasksViewState.getBundle().getUsers().size() == 1);
        verify(mockDataUseCase, times(1)).getListOffLineFirst(any(GetRequest.class));
    }
}