[![](https://jitpack.io/v/Zeyad-37/RxRedux.svg)](https://jitpack.io/#Zeyad-37/RxRedux)

# RxRedux
A library that manages state using RxJava 2 and Architecture Components.

Medium Post: <https://goo.gl/7oH1B1>

# Getting Started
Project root build.gradle
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        google()
  }
}
```
Module build.gradle
```
dependencies {
    implementation 'com.github.Zeyad-37:RxRedux:3.x.x'
} 
```
# Step1

ViewModels must extend BaseViewModel\<I, R, S, E>. <br />
I are your intents, <br />
R are your Results, <br />
S are your States, <br />
E are your Effects. <br />
There are two abstract methods that you will need to implement.

First, reduceIntentsToResults.
```
override fun reduceIntentsToResults(intent: I, currentState: Any): Flowable<*>
    return when (intent) {
            is GetPaginatedUsersIntent -> when (currentState) {
                is EmptyState, is GetState -> getUsers(intent.getPayLoad())
                else -> throwIllegalStateException(intent)
            }
            is DeleteUsersIntent -> when (currentState) {
                is GetState -> deleteCollection(intent.getPayLoad())
                else -> throwIllegalStateException(intent)
            }
            is SearchUsersIntent -> when (currentState) {
                is GetState -> search(intent.getPayLoad())
                else -> throwIllegalStateException(intent)
            }
            is UserClickedIntent -> when (currentState) {
                is GetState -> Flowable.just(SuccessEffectResult(NavigateTo(intent.getPayLoad()), intent))
                else -> throwIllegalStateException(intent)
            }
        }
}
```
This is a simple mapping function that links every Intent with its corresponding action
function. The rest of the class holds your executables which are methods that return Flowables.

### States vs Effects
A state is view binding that is persistent, while effects are one off. So effects can be used to for navigation  
and UI changes that should not be persisted across the life cycle of the view

Second, a stateReducer method that manages the transition between your success states, by
 implementing StateReducer interface.
PS. BaseViewModel extends ViewModel from Android Architecture Components
```
override fun stateReducer(newResult: R, currentState: S): S {
        return { newResult, currentStateBundle ->
            val currentItemInfo = currentStateBundle?.list?.toMutableList() ?: mutableListOf()
            return when (currentStateBundle) {
                is EmptyState -> when (newResult) {
                    is List<*> -> getListState(newResult, currentItemInfo)
                    else -> throw IllegalStateException("Can not reduce EmptyState with this result: $newResult!")
                }
                is ListState -> when (newResult) {
                    is List<*> -> getListState(newResult, currentItemInfo)
                    else -> throw IllegalStateException("Can not reduce ListState with this result: $newResult!")
                }
                else -> throw IllegalStateException("Can not reduce $currentStateBundle")
            }
        }
    }
}
```
Its a good practice to have a type for every state for your view.

### Middleware
If you want to hookup a crash reporting library or any middleware of any sorts you can override the middleware method
```
override fun middleware(it: PModel<*, I>) { {
    when (it) {
        is SuccessState, is LoadingState -> Crashlytics.log(Log.DEBUG, "PModel", it.toString())
        is ErrorState -> Crashlytics.logException(it.error)
    }
}
```

### Error Message Factory
When any of your actions return an error you can override the `errorMessageFactory` function to generate the correct  
error message depending on the `Intent` that caused it and the throwable that was thrown.

```
override fun errorMessageFactory(throwable: Throwable, intent: I, currentStateBundle: E): String {
    return throwable.message.orEmpty()
}
```

# Step 2
### Option A: Activities/Fragments extend abstract classes
Your Activities or Fragments need to extend BaseActivity<PModel, ViewModel> or
BaseFragment<PModel, ViewModel>. These base classes handle life cycle events. You will need to
implement 7 methods and initialize your Intents stream, more on that in a bit. <br />
First method: initialize(). You should instantiate all your dependencies here, including your ViewModels.<br />
Second method: setupUI(). Here you setContentView() and all other ui related stuff.<br />
Third method: showError(String message). Given the error message, provide an implementation to display it on the screen. Could be SnackBars, Toast messages, error dialogs or whatever.<br />
Forth method: toggleViews(boolean isLoading). Given a boolean value indicating if the current state is a loading state, you should enable/disable buttons, hide/show progress bars and so on.<br />
Fifth method: renderSuccessState(S state). Given a state, provide an implementation to display thatsuccess state.<br />
Sixth method: initialStateProvider(). Provide the initial state of the view.<br />
Seventh method: intents(). Provide an Observable of the intents.<br />
### Option B: Activities/Fragments implement BaseActivity/Fragment interfaces
Activities/Fragments will override the viewModel and viewState from the interface 
IBaseActivity/Fragment
```
class UserListActivity() : BaseActivity<UserListIntents, UserListResult, UserListState, UserListEffect, UserListVM>() {}

class UserListActivity2
    : AppCompatActivity(), IBaseActivity<UserListIntents, UserListResult, UserListState, UserListEffect, UserListVM> {
    
    override lateinit var intentStream: Observable<UserListIntents> // <== intentStream to be initialized 
    override lateinit var viewModel: UserListVM
    override lateinit var viewState: UserListState
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateImpl(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        onStartImpl()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        onRestoreInstanceStateImpl(savedInstanceState)
    }

    override fun initialize() {
        viewModel = getViewModel()
        if (viewState == null) {
            intentObservable = Single.just<Any>(GetPaginatedUsersIntent(0))
                .doOnSuccess { Log.d("GetPaginatedUsersIntent", FIRED) }.toObservable()
        }
    }
    
    override fun setupUI(isNew: Boolean) {
        setContentView(R.layout.activity_user_list)
        // ...
    }
    
    override fun initialState(): UserListState = UserListState()
    
    override fun renderSuccessState(successState: UserListState) {
        when (successState) {
            is EmptyState -> // Your Implementation here
            is ListState -> // Your Implementation here
            else -> throw IllegalStateException("Can not render $successState")
       }
    }
    
    override fun toggleViews(isLoading: Boolean, intent: I) {
        // Your Implementation here
    }
    
    override fun showError(errorMessage: String, cause: Throwable, intent: I) {
        showErrorSnackBar(message, anyView, LENGTH_LONG);
    }
    
    // An example on how to merge post OnResume generated intents
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.selected_list_menu, menu)
        menu.findItem(R.id.delete_item).setOnMenuItemClickListener {
            viewModel.offer(DeleteUsersIntent(Observable.fromIterable(usersAdapter.selectedItems)
                .map<String> { itemInfo -> itemInfo.getData<User>().login }.toList()
                .blockingGet()))
                true
        }
        return true
    }
}
```
Your intents should collect the needed input and encapsulate it in an object of type `I`.
And your done. So lets recap

# Be Aware

Everything executed in Views are on the Main Thread, while everything executed on the ViewModel are
on the Computation Scheduler.

Un/Subscribing from the streams are handled automatically with LiveData and happen on onCreate/onDestroy

Since the library is written in Kotlin there are no nullable objects used or allowed, only the
viewState is null until you provide the initialization.

# Benefits

Applying this pattern, we ensure:
That all our intents(inputs) pass through 1 stream, which is a nice way to clarify and organize what are the possible actions allowed on the view.
Single source of truth to the current PModel, which is automatically persisted in instanceState, needs to implement Parcelable.
Error handling is an ease since we can map Throwables to messages and display them as we see fit.
Loading States are also an ease, through the toggle(boolean isLoading) callback that signals whenever the load state starts or ends.
Transition between success states is more clear through the StateReducer and the renderSuccessState() call back
We crash the app if something outside the states and intents we have declared causes any unexpected behavior.

# License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
