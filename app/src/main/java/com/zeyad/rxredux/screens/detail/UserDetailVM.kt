package com.zeyad.rxredux.screens.detail

import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.core.viewmodel.throwIllegalStateException
import com.zeyad.rxredux.utils.Constants.URLS.REPOSITORIES
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.requests.GetRequest
import io.reactivex.Flowable
import io.reactivex.Observable

class UserDetailVM(private val dataUseCase: IDataService) :
        BaseViewModel<UserDetailResult, UserDetailState, UserDetailEffect>() {

    override fun reducer(newResult: UserDetailResult, currentStateBundle: UserDetailState): UserDetailState {
        return when (currentStateBundle) {
            is IntentBundleState -> when (newResult) {
                is ListRepository -> FullDetailState(currentStateBundle.isTwoPane, currentStateBundle.user,
                        Observable.fromIterable(newResult.repos)
                                .map { ItemInfo(it, R.layout.repo_item_layout) }
                                .toList(newResult.repos.size).blockingGet())
            }
            is FullDetailState -> currentStateBundle.throwIllegalStateException(newResult)
        }
    }

    override fun reduceEventsToResults(event: BaseEvent<*>, currentStateBundle: Any): Flowable<*> {
        return when (val userDetailEvents = event as UserDetailEvents) {
            is GetReposEvent -> getRepositories(userDetailEvents.getPayLoad())
            is NavigateToEvent -> Flowable.just(SuccessEffectResult(Pair(userDetailEvents.getPayLoad(), false), event))
        }
    }

    private fun getRepositories(userLogin: String): Flowable<ListRepository> =
            dataUseCase.getList<Repository>(GetRequest.Builder(Repository::class.java, true)
                    .url(String.format(REPOSITORIES, userLogin)).build()).map { ListRepository(it) }
//            dataUseCase.queryDisk(object : RealmQueryProvider<Repository> {
//                override fun create(realm: Realm): RealmQuery<Repository> =
//                        realm.where(Repository::class.java).equalTo("owner.login", userLogin)
//            }).flatMap { list ->
//                if (list.isNotEmpty())
//                    Flowable.just(list)
//                else
//                    dataUseCase.getList(GetRequest.Builder(Repository::class.java, true)
//                            .url(String.format(REPOSITORIES, userLogin)).build())
//            }.map { ListRepository(it) }
}
