package com.zeyad.rxredux.screens.user.list

import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.BaseViewModel
import com.zeyad.rxredux.core.StateReducer
import com.zeyad.rxredux.screens.user.User
import com.zeyad.rxredux.screens.user.list.events.DeleteUsersEvent
import com.zeyad.rxredux.screens.user.list.events.GetPaginatedUsersEvent
import com.zeyad.rxredux.screens.user.list.events.SearchUsersEvent
import com.zeyad.rxredux.utils.Constants.URLS.USER
import com.zeyad.rxredux.utils.Constants.URLS.USERS
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.util.*

/**
 * @author ZIaDo on 6/8/18.
 */
class UserListVM(private var dataUseCase: IDataService) : BaseViewModel<UserListState>() {

    override fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>> {
        return Function { event ->
            when (event) {
                is GetPaginatedUsersEvent -> getUsers(event.getPayLoad())
                is DeleteUsersEvent -> deleteCollection(event.getPayLoad())
                is SearchUsersEvent -> search(event.getPayLoad())
                else -> Flowable.empty<Any>()
            }
        }
    }

    override fun stateReducer(): StateReducer<UserListState> {
        return object : StateReducer<UserListState> {
            override fun reduce(newResult: Any?, event: String?, currentStateBundle: UserListState?): UserListState {
                var users: MutableList<User>
                users = if (currentStateBundle?.users == null)
                    ArrayList()
                else
                    Observable.fromIterable(currentStateBundle.users)
                            .map<User> { it.getData() }
                            .toList().blockingGet()
                val searchList = ArrayList<User>()
                when (event) {
                    "GetPaginatedUsersEvent" -> users.addAll(newResult as List<User>)
                    "SearchUsersEvent" -> searchList.addAll(newResult as List<User>)
                    "DeleteUsersEvent" -> users = Observable.fromIterable(users)
                            .filter { user -> !(newResult as List<*>).contains(user.login) }
                            .distinct().toList().blockingGet()
                    else -> {
                    }
                }
                return UserListState.builder()
                        .users(users)
                        .searchList(searchList)
                        .lastId(users[users.size - 1].id.toLong())
                        .build()
            }
        }
    }

    private fun getUsers(lastId: Long): Flowable<List<User>> {
        return if (lastId == 0L)
            dataUseCase.getListOffLineFirst(GetRequest.Builder(User::class.java, true)
                    .url(String.format(USERS, lastId))
                    .cache(User.LOGIN)
                    .build())
        else
            dataUseCase.getList(GetRequest.Builder(User::class.java, true)
                    .url(String.format(USERS, lastId)).build())
    }

    private fun search(query: String): Flowable<List<User>> {
        return dataUseCase
                .queryDisk<User>(RealmQueryProvider { it.where(User::class.java).beginsWith(User.LOGIN, query) })
                .zipWith(dataUseCase.getObject<User>(GetRequest.Builder(User::class
                        .java,
                        false)
                        .url(String.format(USER, query)).build())
                        .onErrorReturnItem(User())
                        .filter { user -> user.id != 0 }
                        .map { mutableListOf(it) },
                        BiFunction<MutableList<User>, List<User>, List<User>>
                        { users, singleton ->
                            users.addAll(singleton)
                            users.toSet().toList()
                        })
    }

    private fun deleteCollection(selectedItemsIds: List<String>): Flowable<List<String>> {
        return dataUseCase.deleteCollectionByIds<Any>(PostRequest.Builder(User::class.java, true).payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String::class.java).cache().build()).map { o -> selectedItemsIds }
    }
}