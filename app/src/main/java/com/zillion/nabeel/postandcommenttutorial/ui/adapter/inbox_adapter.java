package com.zillion.nabeel.postandcommenttutorial.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zillion.nabeel.postandcommenttutorial.R;
import com.zillion.nabeel.postandcommenttutorial.models.User;
import com.zillion.nabeel.postandcommenttutorial.models.inbox_model;
import com.zillion.nabeel.postandcommenttutorial.utils.FirebaseUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class inbox_adapter extends ArrayAdapter<inbox_model> {

    private LayoutInflater mInflater;
    private List<inbox_model> mUsers = null;
    private int layoutResource;
    private Context mContext;


    public inbox_adapter(@NonNull Context context, int resource, @NonNull List<inbox_model> objects) {
        super(context, resource, objects);

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{

        TextView username;
        TextView Message;
        TextView timestamp;
        CircleImageView profileimage;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){

            convertView = mInflater.inflate(layoutResource, parent , false);
            viewHolder = new ViewHolder();

            viewHolder.username = (TextView) convertView.findViewById(R.id.inbox_sender_name_tv);
            viewHolder.Message = (TextView) convertView.findViewById(R.id.inbox_message_tv);
            viewHolder.timestamp = (TextView) convertView.findViewById(R.id.inbox_timestamp);
            viewHolder.profileimage = (CircleImageView) convertView.findViewById(R.id.inbox_sender_imageview);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

//        Toast.makeText(getContext(), getItem(position).getMessage(), Toast.LENGTH_SHORT).show();

        viewHolder.username.setText(getItem(position).getReceiver_name());
        viewHolder.Message.setText(getItem(position).getMessage());
        viewHolder.timestamp.setText(DateUtils.getRelativeTimeSpanString(getItem(position).getSending_timeStamp()));

        if(FirebaseUtils.getCurrentUser().getEmail().equals(getItem(position).getSender_email())){

            Glide.with(mContext).load(getItem(position).getReceiver_image_uri()).into(viewHolder.profileimage);

        } else {

            Glide.with(mContext).load(getItem(position).getSender_image_url()).into(viewHolder.profileimage);

        }
//        Glide.with(mContext).load(getItem(position).getSender_image_url()).into(viewHolder.profileimage);

        return convertView;
    }
}
