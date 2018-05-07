package com.zeyad.rxredux.screens.user.list.viewHolders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import com.zeyad.rxredux.screens.user.User
import kotlinx.android.synthetic.main.user_item_layout.view.*

/**
 * @author zeyad on 12/1/16.
 */
class UserViewHolder(itemView: View) : GenericRecyclerViewAdapter.GenericViewHolder<User>(itemView) {

    override fun bindData(userModel: User, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        if (userModel.avatarUrl!!.isNotEmpty()) {
            Glide.with(itemView.context).load(userModel.avatarUrl).into(itemView.avatar)
        } else {
            Glide.with(itemView.context)
                    .load(if ((Math.random() * 10).toInt() % 2 == 0)
                        "https://github.com/identicons/jasonlong.png"
                    else
                        "https://help.github.com/assets/images/help/profile/identicon.png")
                    .into(itemView.avatar)
        }
        if (userModel.login!!.isNotEmpty()) {
            itemView.title.text = userModel.login
        }
        itemView.setBackgroundColor(if (isItemSelected) Color.GRAY else Color.WHITE)
    }

    fun getAvatar(): ImageView = itemView.avatar
    fun getTextViewTitle(): TextView = itemView.title
}
