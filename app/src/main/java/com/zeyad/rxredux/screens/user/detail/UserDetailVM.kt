package com.zeyad.rxredux.screens.user.detail

import android.content.Intent
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.utils.Constants.URLS.REPOSITORIES
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.GetRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmQuery

class UserDetailVM(private val dataUseCase: IDataService) : BaseViewModel<UserDetailState>() {

    override fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>) = throwable.localizedMessage!!

    override fun stateReducer(newResult: Any, event: BaseEvent<*>, currentStateBundle: UserDetailState): UserDetailState {
        return when (currentStateBundle) {
            is IntentBundleState -> when (newResult) {
                is Pair<*, *> -> NavigateFromDetail(newResult.first as Intent, newResult.second as Boolean)
                is List<*> -> FullDetailState(currentStateBundle.isTwoPane, currentStateBundle.user,
                        Observable.fromIterable(newResult as List<Repository>)
                                .map { ItemInfo(it, R.layout.repo_item_layout) }
                                .toList(newResult.size).blockingGet())
                else ->
                    throw IllegalStateException("Can not reduce ${currentStateBundle.javaClass} with this result: $newResult!")
            }
            is FullDetailState -> when (newResult) {
                is Pair<*, *> -> NavigateFromDetail(newResult.first as Intent, newResult.second as Boolean)
                else ->
                    throw IllegalStateException("Can not reduce ${currentStateBundle.javaClass} with this result: $newResult!")
            }
            else ->
                throw IllegalStateException("Can not reduce ${currentStateBundle.javaClass} with this result: $newResult!")
        }
    }

    override fun mapEventsToActions(event: BaseEvent<*>): Flowable<*> {
        val userDetailEvents = event as UserDetailEvents
        return when (userDetailEvents) {
            is GetReposEvent -> getRepositories(userDetailEvents.getPayLoad())
            is NavigateToEvent -> Flowable.just(Pair(userDetailEvents.getPayLoad(), false))
        }
    }

    private fun getRepositories(userLogin: String): Flowable<List<Repository>> =
            dataUseCase.queryDisk(object : RealmQueryProvider<Repository> {
                override fun create(realm: Realm): RealmQuery<Repository> =
                        realm.where(Repository::class.java).equalTo("owner.login", userLogin)
            }).flatMap { list ->
                if (list.isNotEmpty())
                    Flowable.just(list)
                else
                    dataUseCase.getList(GetRequest.Builder(Repository::class.java, true)
                            .url(String.format(REPOSITORIES, userLogin)).build())
            }
}
