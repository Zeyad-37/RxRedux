package com.zeyad.rxredux.core.redux

import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Function

class UserListVM(private val dataUseCase: IDataService) : BaseViewModel<UserListState>() {

    override fun stateReducer(): (newResult: Any, event: BaseEvent<*>, currentStateBundle: UserListState) -> UserListState {
        return { newResult, event, currentStateBundle ->
            var users: MutableList<User> = if (currentStateBundle.getUsers() == null)
                mutableListOf()
            else
                Observable.fromIterable<ItemInfo>(currentStateBundle.getUsers())
                        .map { it.getData<User>() }
                        .toList().blockingGet()
            val searchList = mutableListOf<User>()
            when (event) {
                is GetPaginatedUsersEvent -> users.addAll(newResult as List<User>)
                is SearchUsersEvent -> searchList.addAll(newResult as List<User>)
                is DeleteUsersEvent -> users = Observable.fromIterable(users)
                        .filter { user -> !(newResult as List<*>).contains(user.getLogin()) }
                        .distinct().toList().blockingGet()
                else -> {
                }
            }
            UserListState.builder()
                    .users(users)
                    .searchList(searchList)
                    .lastId(users[users.size - 1].getId().toLong())
                    .build()
        }
    }

    override fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>> {
        return Function { event ->
            when (event) {
                is GetPaginatedUsersEvent -> getUsers(event.getPayLoad())
                is DeleteUsersEvent -> deleteCollection(event.getPayLoad())
//                is SearchUsersEvent -> search(stateEvent.payLoad)
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }

    private fun getUsers(lastId: Long): Flowable<List<User>> {
        return if (lastId == 0L)
            dataUseCase.getListOffLineFirst(GetRequest.Builder(User::class.java, true)
                    .url(String.format("user/%s", lastId))
                    .cache(User.LOGIN)
                    .build())
        else
            dataUseCase.getList(GetRequest.Builder(User::class.java, true)
                    .url(String.format("", lastId)).build())
    }

//    private fun search(query: String): Flowable<List<User>> {
//        return dataUseCase.queryDisk<User> { realm -> realm.where(User::class.java).beginsWith(User.LOGIN, query) }
//                .zipWith(dataUseCase.getObject<User>(GetRequest.Builder(User::class.java, false)
//                        .url(String.format("user/%s", query)).build())
//                        .onErrorReturnItem(User()).filter { user -> user.getId() != 0 }
//                        .map<Any> { user -> listOf(user) },
//                        { users, singleton ->
//                            users.addAll(singleton)
//                            ArrayList<User>(HashSet<User>(users))
//                        } as BiFunction<List<User>, List<User>, List<User>>)
//    }

    private fun deleteCollection(selectedItemsIds: List<String>): Flowable<List<String>> {
        return dataUseCase.deleteCollectionByIds<Any>(PostRequest.Builder(User::class.java, true).payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String::class.java).cache().build()).map { o -> selectedItemsIds }
    }
}
