package com.zeyad.rxredux.screens.user.list.viewHolders;

import com.bumptech.glide.Glide;
import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.rxredux.R;
import com.zeyad.rxredux.screens.user.list.User;
import com.zeyad.rxredux.utils.Utils;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 12/1/16.
 */
public class UserViewHolder extends GenericRecyclerViewAdapter.ViewHolder<User> {
    @BindView(R.id.title)
    TextView textViewTitle;

    @BindView(R.id.avatar)
    ImageView avatar;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(User userModel, boolean isItemSelected, int position, boolean isEnabled) {
        if (userModel != null) {
            if (Utils.isNotEmpty(userModel.getAvatarUrl())) {
                Glide.with(itemView.getContext()).load(userModel.getAvatarUrl()).into(avatar);
            } else {
                Glide.with(itemView.getContext())
                        .load(((int) (Math.random() * 10)) % 2 == 0 ? "https://github.com/identicons/jasonlong.png"
                                : "https://help.github.com/assets/images/help/profile/identicon.png")
                        .into(avatar);
            }
            if (Utils.isNotEmpty(userModel.getLogin())) {
                textViewTitle.setText(userModel.getLogin());
            }
        }
        itemView.setBackgroundColor(isItemSelected ? Color.GRAY : Color.WHITE);
    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public ImageView getAvatar() {
        return avatar;
    }
}
