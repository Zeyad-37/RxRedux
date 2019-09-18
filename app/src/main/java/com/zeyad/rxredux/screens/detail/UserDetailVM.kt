package com.zeyad.rxredux.screens.detail

import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.core.viewmodel.throwIllegalStateException
import com.zeyad.rxredux.utils.Constants.URLS.REPOSITORIES
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.requests.GetRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class UserDetailVM(private val dataUseCase: IDataService) :
        BaseViewModel<UserDetailEvents<*>, UserDetailResult, UserDetailState, UserDetailEffect>() {
    override var disposable: Disposable = CompositeDisposable()

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

    override fun reduceEventsToResults(event: UserDetailEvents<*>, currentState: Any): Flowable<*> {
        return when (event) {
            is GetReposEvent -> getRepositories(event.getPayLoad())
            is NavigateToEvent -> Flowable.just(SuccessEffectResult(Pair(event.getPayLoad(), false), event))
        }
    }

    private fun getRepositories(userLogin: String): Flowable<ListRepository> =
//            dataUseCase.queryDisk(object : RealmQueryProvider<Repository> {
//                override fun create(realm: Realm): RealmQuery<Repository> =
//                        realm.where(Repository::class.java).equalTo("owner.login", userLogin)
//            }).flatMap { list ->
//                if (list.isNotEmpty())
//                    Flowable.just(list)
//                else
            dataUseCase.getList<Repository>(GetRequest.Builder(Repository::class.java, true)
                    .url(String.format(REPOSITORIES, userLogin)).build())
//            }
                    .map { ListRepository(it) }
}
