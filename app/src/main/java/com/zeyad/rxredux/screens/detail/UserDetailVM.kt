package com.zeyad.rxredux.screens.detail

import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.core.viewmodel.coroutines.Machine
import com.zeyad.rxredux.core.viewmodel.throwIllegalStateException
import com.zeyad.rxredux.screens.User
import com.zeyad.usecases.api.IDataService
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserDetailVM(private val dataUseCase: IDataService) :
        Machine<UserDetailEvents<*>, UserDetailResult, UserDetailState, UserDetailEffect>() {

    override fun stateReducer(newResult: UserDetailResult, currentState: UserDetailState): UserDetailState {
        return when (currentState) {
            is IntentBundleState -> when (newResult) {
                is ListRepository -> FullDetailState(currentState.isTwoPane, currentState.user,
                        Observable.fromIterable(newResult.repos)
                                .map { ItemInfo(it, R.layout.repo_item_layout) }
                                .toList(newResult.repos.size).blockingGet())
            }
            is FullDetailState -> throwIllegalStateException(newResult)
        }
    }

    override fun reduceEventsToResults(event: UserDetailEvents<*>, currentState: Any): Flow<*> {
        return when (event) {
            is GetReposEvent -> getRepositories(event.getPayLoad())
            is NavigateToEvent -> flowOf(SuccessEffectResult(Pair(event.getPayLoad(), false), event))
        }
    }

    private fun getRepositories(userLogin: String): Flow<ListRepository> =
//            dataUseCase.queryDisk(object : RealmQueryProvider<Repository> {
//                override fun create(realm: Realm): RealmQuery<Repository> =
//                        realm.where(Repository::class.java).equalTo("owner.login", userLogin)
//            }).flatMap { list ->
//                if (list.isNotEmpty())
//                    Flowable.just(list)
//                else
            flowOf(ListRepository(listOf(Repository(1, "Repo", User("Sayed")))))
//            dataUseCase.getList<Repository>(GetRequest.Builder(Repository::class.java, true)
//                    .url(String.format(REPOSITORIES, userLogin)).build())
//            }
//                    .map { ListRepository(it) }
}
