package com.zeyad.rxredux.screens.detail

import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.StringMessage
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.utils.Constants.URLS.REPOSITORIES
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.GetRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.RealmQuery

class UserDetailVM(private val dataUseCase: IDataService) :
        BaseViewModel<UserDetailResult, UserDetailState, UserDetailEffect>() {
    override var disposable: CompositeDisposable = CompositeDisposable()

    override fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>) =
            StringMessage(throwable.localizedMessage)

    override fun reducer(newResult: UserDetailResult, event: BaseEvent<*>, currentStateBundle: UserDetailState): UserDetailState {
        return when (currentStateBundle) {
            is IntentBundleState -> when (newResult) {
                is ListRepository -> FullDetailState(currentStateBundle.isTwoPane, currentStateBundle.user,
                        Observable.fromIterable(newResult.repos)
                                .map { ItemInfo(it, R.layout.repo_item_layout) }
                                .toList(newResult.repos.size).blockingGet())
            }
            is FullDetailState -> throw IllegalStateException("Can not reduce ${currentStateBundle.javaClass} with this result: $newResult!")
        }
    }

    override fun mapEventsToActions(event: BaseEvent<*>, currentStateBundle: UserDetailState): Flowable<*> {
        val userDetailEvents = event as UserDetailEvents
        return when (userDetailEvents) {
            is GetReposEvent -> getRepositories(userDetailEvents.getPayLoad())
            is NavigateToEvent -> Flowable.just(SuccessEffectResult(Pair(userDetailEvents.getPayLoad(), false), event))
        }
    }

    private fun getRepositories(userLogin: String): Flowable<ListRepository> =
            dataUseCase.queryDisk(object : RealmQueryProvider<Repository> {
                override fun create(realm: Realm): RealmQuery<Repository> =
                        realm.where(Repository::class.java).equalTo("owner.login", userLogin)
            }).flatMap { list ->
                if (list.isNotEmpty())
                    Flowable.just(list)
                else
                    dataUseCase.getList(GetRequest.Builder(Repository::class.java, true)
                            .url(String.format(REPOSITORIES, userLogin)).build())
            }.map { ListRepository(it) }
}
