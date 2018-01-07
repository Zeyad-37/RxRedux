package com.zeyad.rxredux.screens.user;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.zeyad.rxredux.screens.user.detail.UserDetailVM;
import com.zeyad.rxredux.screens.user.list.UserListVM;

/**
 * @author ZIaDo on 12/13/17.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListVM.class)) {
            return (T) new UserListVM();
        } else if (modelClass.isAssignableFrom(UserDetailVM.class)) {
            return (T) new UserDetailVM();
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getSimpleName());
    }
}
