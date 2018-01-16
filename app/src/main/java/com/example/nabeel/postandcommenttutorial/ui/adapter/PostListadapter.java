package com.example.nabeel.postandcommenttutorial.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nabeel.postandcommenttutorial.R;
import com.example.nabeel.postandcommenttutorial.models.Post;
import com.example.nabeel.postandcommenttutorial.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        TextView username;
        CircleImageView profile_img;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){

            convertView = mInflater.inflate(layoutResource, parent , false);
            viewHolder = new ViewHolder();

            viewHolder.Post = (TextView) convertView.findViewById(R.id.post_search);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username_post_srch);
            viewHolder.profile_img = (CircleImageView) convertView.findViewById(R.id.profile_image_post_srch);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.Post.setText(getItem(position).getPostText());
        FirebaseUtils.getUserRef(getItem(position).getEmail().replace(".",","))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String img = dataSnapshot.child("image").getValue().toString();

                        if(!TextUtils.isEmpty(name)){
                            viewHolder.username.setText(name);
                        }

                        if(!TextUtils.isEmpty(img)){
                            Glide.with(mContext).load(img).into(viewHolder.profile_img);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//        viewHolder.username.setText(getItem(position).);

        return convertView;
    }
}
