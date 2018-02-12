package com.zillion.nabeel.postandcommenttutorial.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserListAdapter extends ArrayAdapter<User> {

    private LayoutInflater mInflater;
    private List<User> mUsers = null;
    private int layoutResource;
    private Context mContext;


    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{

        TextView username;
        TextView userType;
        CircleImageView profileimage;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){

            convertView = mInflater.inflate(layoutResource, parent , false);
            viewHolder = new ViewHolder();

            viewHolder.username = (TextView) convertView.findViewById(R.id.username_search);
            viewHolder.userType = (TextView) convertView.findViewById(R.id.usertype_search);

            viewHolder.profileimage = (CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.username.setText(getItem(position).getName());
        viewHolder.userType.setText(getItem(position).getUserType());

        Glide.with(mContext).load(getItem(position).getImage()).into(viewHolder.profileimage);

        return convertView;
    }
}
