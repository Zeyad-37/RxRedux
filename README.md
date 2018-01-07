[![](https://jitpack.io/v/Zeyad-37/RxRedux.svg)](https://jitpack.io/#Zeyad-37/RxRedux)

# RxRedux
A library that manages state using RxJava 2.

Medium Post: https://goo.gl/7oH1B1

# Getting Started
Project root build.gradle
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        maven { url 'https://maven.google.com' } 
  }
}
```
Module build.gradle
```
dependencies {
    compile 'com.github.Zeyad-37:RxRedux:x.x.x'
} 
```
## Step1

ViewModels must extend BaseViewModel\<S\>. S is your UIState. There are two abstract methods that you will need to implement. 
First, an
 init method to initialize your ViewModel, by passing your initial state S and your SuccessStateAccumulator\<S\>, explanation coming shortly.
PS. BaseViewModel extends ViewModel from Android Architecture Components
```
@Override
public void init(UserListState state, Object... otherDependencies) {
    dataUseCase = (IDataService) otherDependencies[0];
    setInitialState(state);
}
```
Secondly, your EventsToExecutablesMapper.
```
@Override
public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
    return event -> {
        Flowable executable = Flowable.empty();
        if (event instanceof GetPaginatedUsersEvent) {
            executable = getUsers(((GetPaginatedUsersEvent) event).getLastId());
        } else if (event instanceof DeleteUsersEvent) {
            executable = deleteCollection(((DeleteUsersEvent) event).getSelectedItemsIds());
        }
        return executable;
    };
}
```
This is a simple mapping function that links everyEvent with its corresponding executable function. The rest of the class holds your executables which are methods that return observables.

## Step 2
The StateReducer is an interface that you implement that handles how your view should transition
from one success state to the other,
given a new result, name of the event that triggered that result and the current UIState. Lives in your Activity or Fragment.
```
SuccessStateAccumulator accumulator = 
new StateReducer<UserListState>() {
    @Override
    public UserListState reduce(Object newResult, String event, UserListState currentStateBundle) {
        List<User> resultList = (List) newResult;
        List<User> users = currentStateBundle == null ? new ArrayList<>() : currentStateBundle.getUsers();
        List<User> searchList = new ArrayList<>();
        switch (event) {
            case "GetPaginatedUsersEvent":
                users.addAll(resultList);
                break;
            case "DeleteUsersEvent":
                users = Observable.fromIterable(users)
                                  .filter(user -> !resultList.contains((long) user.getId()))
                                  .distinct()
                                  .toList()
                                  .blockingGet();
                break;
        }
        return UserListState.builder().users(users).build();
    }
};
```
## Step 3
Your Activities or Fragments need to extend BaseActivity<UIState, ViewModel> or BaseFragment<UIState, ViewModel>. These base classes handle life cycle events. You will need to implement 6 methods and initialize your Events stream, more on that in a bit.
First method: initialize(). You should insatiate all your dependencies here, including your ViewModels and Events stream.
Second method: setupUI(). Here you setContentView() and all other ui related stuff.
Third method: errorMessageFactory(). Its a method that returns an interface that when given a Throwable it should return a String error message.
Fourth method: showError(String message). Given the error message, provide an implementation to display it on the screen. Could be SnackBars, Toast messages, error dialogs or whatever.
Fifth method: toggleViews(boolean isLoading). Given a boolean value indicating if the current state is a loading state, you should enable/disable buttons, hide/show progress bars and so on.
Sixth method: renderState(S successState). Given a state, provide an implementation to display that success state.
Finally initializing your Event stream.
```
@Override
public void renderState(UserListState successState) {
    viewState = successState;
    usersAdapter.setDataList(viewState.getUsers()));
}
@Override
public void toggleViews(boolean isLoading) {
    loaderLayout.bringToFront();
    loaderLayout.setVisibility(isLoading ? VISIBLE : GONE);
}

@Override
public void showError(String message) {
    showErrorSnackBar(message, anyView, LENGTH_LONG);
}
```

## Step 4: Events Stream
The events stream is an Observable<BaseEvent>. BaseEvent is an empty interface that all your events will need to implement, just for type safety. You initialize your event observable by merging all the events in your view. Like your GetUsersEvent event, DeleteUserEvent, SearchUserEvent, etc. RxBinding2 is a great lib that provides event observables from ui components.
```
@Override
public void initialize() {
    //...
    events = Single.<BaseEvent>just(new GetPaginatedUsersEvent(0))
        .toObservable();
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // ...    
    events = events.mergeWith(RxSearchView.queryTextChanges(searchView)
        .filter(charSequence -> !charSequence.toString().isEmpty())
        .map(query -> new SearchUsersEvent(query.toString()))
        .throttleLast(100, TimeUnit.MILLISECONDS)
        .debounce(200, TimeUnit.MILLISECONDS));
}
```
Your events should collect the needed input and encapsulate it in an object that implements the BaseEvent interface.
And your done. So lets recap

# Benefits

Applying this pattern, we ensure:
That all our events(inputs) pass through 1 stream, which is a nice way to clarify and organize what are the possible actions allowed on the view.
Single source of truth to the current UIState, which is automatically persisted in instanceState, needs to be annotated with @Parcel from Parceler.
Error handling is an ease since we can map Throwables to messages and display them as we see fit.
Loading States are also an ease, through the toggle(boolean isLoading) callback that signals whenever the load state starts or ends.
Transition between success states is more clear through the SuccessStateAccumulator and the renderSuccessState() call back
We crash the app if something outside the states and events we have declared causes an unexpected behavior.

# License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
