package com.zeyad.rxredux.core

import android.content.Context
import android.support.annotation.StringRes

sealed class Message

data class StringMessage(val message: String) : Message()
data class IntMessage(@StringRes val messageId: Int) : Message() {
    fun getString(context: Context) = context.getString(messageId)
}

fun Message.getErrorMessage(context: Context): String {
    return when (this) {
        is StringMessage -> message
        is IntMessage -> context.getString(messageId)
    }
}

fun Message.getString(): String {
    return when (this) {
        is StringMessage -> message
        is IntMessage -> throw IllegalAccessException("This is the String resource id!")
    }
}