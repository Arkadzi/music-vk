package me.gumenniy.arkadiy.vkmusic.app.adapter;

import android.content.Context;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.model.Group;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class GroupAdapter extends AbstractListAdapter<Group> {

    public GroupAdapter(Context context, List<Group> data) {
        super(context, data);
    }


    @Override
    protected int[] getTextViewIds() {
        return new int[]{R.id.main};
    }

    @Override
    protected int getLayoutId() {
        return R.layout.icon_list_item;
    }

    @Override
    protected String[] getText(Group item) {
        return new String[]{item.getName()};
    }


}
