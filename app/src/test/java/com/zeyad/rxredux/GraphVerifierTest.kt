package com.zeyad.rxredux

import com.zeyad.rxredux.core.viewmodel.GraphVerifier
import com.zeyad.rxredux.screens.User
import com.zeyad.rxredux.screens.list.*
import com.zeyad.usecases.api.IDataService
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class GraphVerifierTest {

    private lateinit var dataService: IDataService
    @Before
    fun setUp() {
        dataService = mock(IDataService::class.java)
        `when`(dataService.getList<User>(anyObject()))
                .thenReturn(Flowable.just(listOf(User())))
    }

    @Test
    fun verify() {
        val isValid = GraphVerifier(UserListVM(dataService))
                .verify(listOf(GetPaginatedUsersEvent(1),
                        UserClickedEvent(User())),
                        listOf(EmptyState(), GetState()),
                        listOf(NavigateTo(User())),
                        listOf(UsersResult(listOf(User()))))
        assert(isValid)
    }
}
