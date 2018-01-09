package com.example.nabeel.postandcommenttutorial.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;

import java.util.List;

/**
 * Created by Nabeel on 11/9/2017.
 */

public class PostListadapter extends ArrayAdapter<Post> {
    private LayoutInflater mInflater;
    private List<Post> mUsers = null;
    private int layoutResource;
    private Context mContext;

    public PostListadapter(@NonNull Context context, int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{

        TextView Post;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){

            convertView = mInflater.inflate(layoutResource, parent , false);
            viewHolder = new ViewHolder();

            viewHolder.Post = (TextView) convertView.findViewById(R.id.post_search);


            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.Post.setText(getItem(position).getPostText());


        return convertView;
    }
}
