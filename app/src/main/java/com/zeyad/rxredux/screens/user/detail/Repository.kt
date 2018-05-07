package com.zeyad.rxredux.screens.user.detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.zeyad.rxredux.screens.user.User
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

/**
 * @author zeyad on 1/25/17.
 */
@Parcelize
data class Repository(@SerializedName("id")
                      var id: Int = 0,
                      @SerializedName("name")
                      var name: String? = null,
                      @SerializedName("full_name")
                      var fullName: String? = null,
                      @SerializedName("owner")
                      internal var owner: User? = null) : RealmObject(), Parcelable {

    companion object {

        fun isEmpty(repository: Repository?): Boolean {
            return repository == null || repository.name == null && repository.fullName == null && repository.owner == null
        }
    }
}
