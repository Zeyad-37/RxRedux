[![](https://jitpack.io/v/Zeyad-37/RxRedux.svg)](https://jitpack.io/#Zeyad-37/RxRedux)

# RxRedux
A library that manages state using RxJava 2 and Architecture Components.

Medium Post: https://goo.gl/7oH1B1

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
    implementation 'com.github.Zeyad-37:RxRedux:2.x.x'
} 
```
# Step1

ViewModels must extend BaseViewModel\<S\>. S is your UIModel. There are two abstract methods that
you will need to implement.
First, an
 stateReducer method that manages the transition between your success states, by
 implementing StateReducer interface.
PS. BaseViewModel extends ViewModel from Android Architecture Components
```
override fun stateReducer(): (newResult: Any, event: BaseEvent<*>, currentStateBundle: UserListState) -> UserListState {
        return { newResult, event, currentStateBundle ->
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
override fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>> {
    return Function { event ->
        when (event) {
            is GetPaginatedUsersEvent -> getUsers(event.getPayLoad())
            is DeleteUsersEvent -> deleteCollection(event.getPayLoad())
            is SearchUsersEvent -> search(event.getPayLoad())
            else -> throw IllegalStateException("Can not map $event to an action")
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

MIT License

Copyright (c) [2019] [Zeyad Gasser]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.