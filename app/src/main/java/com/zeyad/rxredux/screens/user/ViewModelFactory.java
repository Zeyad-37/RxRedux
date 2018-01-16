package com.zeyad.rxredux.screens.user;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.zeyad.rxredux.screens.user.detail.UserDetailVM;
import com.zeyad.rxredux.screens.user.list.UserListVM;
import com.zeyad.usecases.api.IDataService;

/**
 * @author ZIaDo on 12/13/17.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    final IDataService dataService;

    public ViewModelFactory(IDataService dataService) {
        this.dataService = dataService;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListVM.class)) {
            return (T) new UserListVM(dataService);
        } else if (modelClass.isAssignableFrom(UserDetailVM.class)) {
            return (T) new UserDetailVM(dataService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getSimpleName());
    }
}
