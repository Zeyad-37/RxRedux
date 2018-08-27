package com.zeyad.rxredux.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

/** @author by ZIaDo on 3/27/17.
 */
@Parcelize
class DeleteUsersEvent(private val selectedItemsIds: List<String>) : BaseEvent<List<String>> {

    override fun getPayLoad(): List<String> = selectedItemsIds
}
