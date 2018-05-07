package com.zeyad.rxredux.screens.user.detail

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author by ZIaDo on 4/22/17.
 */
internal class GetReposEvent(private val login: String) : BaseEvent<String> {

    override fun getPayLoad(): String {
        return login
    }
}
