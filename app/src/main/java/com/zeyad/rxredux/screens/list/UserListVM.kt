package com.zeyad.rxredux.screens.list

import android.support.v7.util.DiffUtil
import android.util.Log
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.core.viewmodel.throwIllegalStateException
import com.zeyad.rxredux.screens.User
import com.zeyad.rxredux.screens.UserDiffCallBack
import com.zeyad.rxredux.utils.Constants.URLS.USER
import com.zeyad.rxredux.utils.Constants.URLS.USERS
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable

class UserListVM(private val dataUseCase: IDataService) : BaseViewModel<UserListResult, UserListState, UserListEffect>() {

    override fun reduceEventsToResults(event: BaseEvent<*>, currentStateBundle: Any): Flowable<*> {
        Log.d("UserListVM", "currentStateBundle: $currentStateBundle")
        return when (val userListEvent = event as UserListEvents) {
            is GetPaginatedUsersEvent -> when (currentStateBundle) {
                is EmptyState, is GetState -> getUsers(userListEvent.getPayLoad())
                else -> throwIllegalStateException(userListEvent)
            }
            is DeleteUsersEvent -> when (currentStateBundle) {
                is GetState -> deleteCollection(userListEvent.getPayLoad())
                else -> throwIllegalStateException(userListEvent)
            }
            is SearchUsersEvent -> when (currentStateBundle) {
                is GetState -> search(userListEvent.getPayLoad())
                else -> throwIllegalStateException(userListEvent)
            }
            is UserClickedEvent -> when (currentStateBundle) {
                is GetState -> Flowable.just(SuccessEffectResult(NavigateTo(userListEvent.getPayLoad()), event))
                else -> throwIllegalStateException(userListEvent)
            }
        }
    }

    override fun reducer(newResult: UserListResult, currentStateBundle: UserListState): UserListState {
        val currentItemInfo = currentStateBundle.list.toMutableList()
        return when (currentStateBundle) {
            is EmptyState -> when (newResult) {
                is UsersResult -> {
                    val pair = Flowable.fromIterable(newResult.list)
                            .map { ItemInfo(it, R.layout.user_item_layout, it.id) }
                            .toList().toFlowable()
                            .calculateDiff(currentItemInfo)
                    GetState(pair.first, pair.first[pair.first.size - 1].id, pair.second)
                }
            }
            is GetState -> when (newResult) {
                is UsersResult -> {
                    val pair = Flowable.fromIterable(newResult.list)
                            .map { ItemInfo(it, R.layout.user_item_layout, it.id) }
                            .toList()
                            .map {
                                val list = currentStateBundle.list.toMutableList()
                                list.addAll(it)
                                list.toSet().toMutableList()
                            }.toFlowable()
                            .calculateDiff(currentItemInfo)
                    GetState(pair.first, pair.first[pair.first.size - 1].id, pair.second)
                }
            }
        }
    }

    private fun Flowable<MutableList<ItemInfo>>.calculateDiff(initialList: MutableList<ItemInfo>)
            : Pair<MutableList<ItemInfo>, DiffUtil.DiffResult> =
            scan<Pair<MutableList<ItemInfo>, DiffUtil.DiffResult>>(Pair(initialList,
                    DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))))
            { pair1, next ->
                Pair(next, DiffUtil.calculateDiff(UserDiffCallBack(pair1.first, next)))
            }.skip(1)
                    .blockingFirst()

    private fun getUsers(lastId: Long): Flowable<UsersResult> {
//        return if (lastId == 0L)
//            dataUseCase.getListOffLineFirst(GetRequest.Builder(User::class.java, true)
//                    .url(String.format(USERS, lastId))
//                    .build())
//        else
        return dataUseCase.getList<User>(GetRequest.Builder(User::class.java, true)
                .url(String.format(USERS, lastId)).build())
                .map { UsersResult(it) }
    }

    private fun search(query: String): Flowable<UsersResult> {
        return dataUseCase.getObject<User>(GetRequest.Builder(User::class.java, false)
                .url(String.format(USER, query)).build())
                .onErrorReturnItem(User())
                .filter { user -> user.id != 0L }
                .map { UsersResult(listOf(it)) }
//        return dataUseCase
//                .queryDisk(object : RealmQueryProvider<User> {
//                    override fun create(realm: Realm): RealmQuery<User> =
//                            realm.where(User::class.java).beginsWith(User.LOGIN, query)
//                })
//                .zipWith(dataUseCase.getObject<User>(GetRequest.Builder(User::class.java, false)
//                        .url(String.format(USER, query)).build())
//                        .onErrorReturnItem(User())
//                        .filter { user -> user.id != 0L }
//                        .map { mutableListOf(it) },
//                        BiFunction<List<User>, MutableList<User>, List<User>>
//                        { singleton, users ->
//                            users.addAll(singleton)
//                            users.asSequence().toSet().toList()
//                        }).map { UsersResult(it) }
    }

    private fun deleteCollection(selectedItemsIds: List<String>): Flowable<List<String>> {
        return dataUseCase.deleteCollectionByIds<Any>(PostRequest.Builder(User::class.java, true)
                .payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String::class.java).cache()
                .build())
                .map { selectedItemsIds }
    }
}
