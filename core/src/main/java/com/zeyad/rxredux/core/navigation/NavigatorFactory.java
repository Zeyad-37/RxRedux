package com.zeyad.rxredux.core.navigation;

public final class NavigatorFactory {
    private NavigatorFactory() {
    }

    public static INavigator getInstance() {
        return Navigator.getInstance();
    }
}
