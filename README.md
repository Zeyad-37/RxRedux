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
I are your events, <br />
R are your Results, <br />
S is your UIModel, <br />
E are your Effects. <br />
There are two abstract methods that you will need to implement.
First, a stateReducer method that manages the transition between your success states, by
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

Secondly, mapEventsToActions.
```
override fun reduceEventsToResults(event: I, currentState: Any): Flowable<*>
    return when (event) {
            is GetPaginatedUsersEvent -> when (currentState) {
                is EmptyState, is GetState -> getUsers(event.getPayLoad())
                else -> throwIllegalStateException(event)
            }
            is DeleteUsersEvent -> when (currentState) {
                is GetState -> deleteCollection(event.getPayLoad())
                else -> throwIllegalStateException(event)
            }
            is SearchUsersEvent -> when (currentState) {
                is GetState -> search(event.getPayLoad())
                else -> throwIllegalStateException(event)
            }
            is UserClickedEvent -> when (currentState) {
                is GetState -> Flowable.just(SuccessEffectResult(NavigateTo(event.getPayLoad()), event))
                else -> throwIllegalStateException(event)
            }
        }
}
```
This is a simple mapping function that links every Event with its corresponding action
function. The rest of the class holds your executables which are methods that return Flowables.

### Middleware
If you want to hookup a crash reporting library or any middleware of any sorts you can override the middleware method
```
override fun middleware(): (UIModel<UserListState>) -> Unit {
    return {
        when (it) {
            is SuccessState, is LoadingState -> Crashlytics.log(Log.DEBUG, "UIModel", it.toString())
            is ErrorState -> Crashlytics.logException(it.error)
        }
    }
}
```

# Step 2
### Option A: Activities/Fragments extend abstract classes
Your Activities or Fragments need to extend BaseActivity<UIModel, ViewModel> or
BaseFragment<UIModel, ViewModel>. These base classes handle life cycle events. You will need to
implement 8 methods and initialize your Events stream, more on that in a bit.
First method: initialize(). You should instantiate all your dependencies here, including your ViewModels.
Second method: setupUI(). Here you setContentView() and all other ui related stuff.
Third method: errorMessageFactory(). Its a method that returns an interface that when given a Throwable it should return a String error message.
Fourth method: showError(String message). Given the error message, provide an implementation to display it on the screen. Could be SnackBars, Toast messages, error dialogs or whatever.
Fifth method: toggleViews(boolean isLoading). Given a boolean value indicating if the current state is a loading state, you should enable/disable buttons, hide/show progress bars and so on.
Sixth method: renderSuccessState(S state). Given a state, provide an implementation to display that
success state.
Seventh method: initialState(). Provide the initial state of the view.
Eighth method: events(). Provide an Observable of the events.
### Option B: Activities/Fragments implement BaseActivity/Fragment interfaces
Activities/Fragments will override the viewModel and viewState from the interface 
IBaseActivity/Fragment
```
class UserListActivity() : BaseActivity<UserListState, UserListVM>() {}

class UserListActivity2(override var viewModel: UserListVM?, override var viewState: UserListState?)
    : AppCompatActivity(), IBaseActivity<UserListState, UserListVM> {
    
    constructor() : this(null, null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
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
            eventObservable = Single.just<BaseEvent<*>>(GetPaginatedUsersEvent(0))
                .doOnSuccess { Log.d("GetPaginatedUsersEvent", FIRED) }.toObservable()
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
    
    override fun toggleViews(isLoading: Boolean, event: BaseEvent<*>) {
        // Your Implementation here
    }
    
    override fun showError(errorMessage: String, event: BaseEvent<*>) {
        showErrorSnackBar(message, anyView, LENGTH_LONG);
    }
    
     override fun errorMessageFactory(): ErrorMessageFactory {
            return { throwable, event: BaseEvent<*> -> throwable.localizedMessage }
        }
    
    override fun events(): Observable<BaseEvent<*>> {
        return eventObservable.mergeWith(postOnResumeEvents())
    }
    
    // An example on how to merge post OnResume generated events
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.selected_list_menu, menu)
        menu.findItem(R.id.delete_item).setOnMenuItemClickListener {
            postOnResumeEvents.onNext(DeleteUsersEvent(Observable.fromIterable(usersAdapter.selectedItems)
                .map<String> { itemInfo -> itemInfo.getData<User>().login }.toList()
                .blockingGet()))
                true
        }
        return true
    }
}
```
Your events should collect the needed input and encapsulate it in an object that implements the BaseEvent interface.
And your done. So lets recap

# Be Aware

Everything executed in Views are on the Main Thread, while everything executed on the ViewModel are
on the Computation Scheduler.

Un/Subscribing from the streams are handled automatically with LiveData and happen on Start/Stop

Since the library is written in Kotlin there are no nullable objects used or allowed, only the
viewState is null until you provide the initialization.

# Benefits

Applying this pattern, we ensure:
That all our events(inputs) pass through 1 stream, which is a nice way to clarify and organize what are the possible actions allowed on the view.
Single source of truth to the current UIModel, which is automatically persisted in instanceState,
 needs to implement Parcelable.
Error handling is an ease since we can map Throwables to messages and display them as we see fit.
Loading States are also an ease, through the toggle(boolean isLoading) callback that signals whenever the load state starts or ends.
Transition between success states is more clear through the StateReducer and the renderSuccessState() call back
We crash the app if something outside the states and events we have declared causes any unexpected behavior.

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
