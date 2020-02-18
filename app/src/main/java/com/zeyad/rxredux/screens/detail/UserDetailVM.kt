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

class UserDetailVM(private val dataUseCase: IDataService) :
        BaseViewModel<UserDetailIntents, UserDetailResult, UserDetailState, UserDetailEffect>() {

    override fun stateReducer(newResult: UserDetailResult, currentState: UserDetailState): UserDetailState {
        return when (currentState) {
            is IntentBundleState -> when (newResult) {
                is ListRepository -> FullDetailState(currentState.user,
                        newResult.repos.map { ItemInfo(it, R.layout.repo_item_layout) })
            }
            is FullDetailState -> throwIllegalStateException(newResult)
        }
    }

    override fun reduceIntentsToResults(intent: UserDetailIntents, currentState: UserDetailState): Flowable<*> {
        return when (intent) {
            is GetReposIntent -> getRepositories(intent.login)
            is NavigateToIntent -> Flowable.just(SuccessEffectResult(Pair(intent.intent, false), intent))
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
