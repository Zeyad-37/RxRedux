package com.zeyad.rxredux.screens.user.detail

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

/**
 * @author by ZIaDo on 4/22/17.
 */
@Parcelize
internal class GetReposEvent(private val login: String) : BaseEvent<String> {

    override fun getPayLoad(): String = login
}
