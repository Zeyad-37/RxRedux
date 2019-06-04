package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.*
import com.zeyad.rxredux.screens.User
import com.zeyad.usecases.api.IDataService
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class GraphVerifierTest {

    private val user = User()
    private lateinit var mockDataUseCase: IDataService
    private lateinit var viewModel: UserListVM
    private val graphVerifier = GraphVerifier()

    @Before
    fun setUp() {
        mockDataUseCase = mock(IDataService::class.java)
        viewModel = UserListVM(mockDataUseCase)
    }

    @Test
    fun verify() {
        assertTrue(graphVerifier.verify(viewModel, listOf(GetPaginatedUsersEvent(1), UserClickedEvent(user)),
                listOf(EmptyState(), GetState()),
                listOf(NavigateTo(user)),
                listOf(EmptyResult, UsersResult(listOf(user)))))
    }
}
