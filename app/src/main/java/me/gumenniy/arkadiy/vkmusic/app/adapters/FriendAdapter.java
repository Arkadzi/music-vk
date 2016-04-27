package me.gumenniy.arkadiy.vkmusic.app.adapters;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.utils.CircleTransform;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class FriendAdapter extends AbstractListAdapter<Friend> {

    public FriendAdapter(Context context, List<Friend> data) {
        super(context, data);
    }

    @Override
    protected void handleImageView(Friend item, ImageView imageView) {
        Picasso.with(getContext()).load(item.getPhoto()).transform(new CircleTransform()).into(imageView);
    }

    @Override
    protected int getImageViewId() {
        return R.id.avatar;
    }

    @Override
    protected int[] getTextViewIds() {
        return new int[] {R.id.main};
    }

    @Override
    protected int getLayoutId() {
        return R.layout.icon_list_item;
    }

    @Override
    protected String[] getText(Friend item) {
        return new String[] {String.format("%s %s", item.getFirstName(), item.getLastName())};
    }


}
