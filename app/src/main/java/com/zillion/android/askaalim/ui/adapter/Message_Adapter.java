package com.zillion.android.askaalim.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zillion.android.askaalim.R;
import com.zillion.android.askaalim.models.Chat_model;
import com.zillion.android.askaalim.utils.FirebaseUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.MessageviewHolder>{

    private List<Chat_model> mMessageList;
    private Context context;

    public Message_Adapter(List<Chat_model> mMessageList, Context context){

        this.mMessageList = mMessageList;
        this.context = context;

    }

    @Override
    public MessageviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_message_layout, parent, false);

        return new MessageviewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageviewHolder holder, int i) {

        String current_user_id = FirebaseUtils.getCurrentUser().getEmail().replace(".",",");

        Chat_model c = mMessageList.get(i);

        String from_user = c.getFrom();

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.weight = 1.0f;
//        params.gravity = Gravity.TOP;

        if(current_user_id.equals(from_user)){

            holder.messageText.setBackgroundResource(R.drawable.my_message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.linearLayout.setGravity(Gravity.RIGHT);
//            params.gravity = Gravity.RIGHT;
            holder.profile_image.setVisibility(View.GONE);

        } else {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.linearLayout.setGravity(Gravity.LEFT);
//            params.gravity = Gravity.LEFT;
            holder.profile_image.setVisibility(View.VISIBLE);

        }

//        holder.messageText.setLayoutParams(params);

        holder.messageText.setText(c.getMessage());
        holder.time_textview.setText(DateUtils.getRelativeTimeSpanString(c.getSending_timeStamp()));

        if(!TextUtils.isEmpty(c.getFrom_image_url())) {
            Glide.with(context).load(c.getFrom_image_url()).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageviewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profile_image;
        public TextView time_textview;
        public LinearLayout linearLayout;

        public MessageviewHolder(View itemView) {
            super(itemView);

            messageText = ((TextView) itemView.findViewById(R.id.message_text));
            time_textview = ((TextView) itemView.findViewById(R.id.timeStamp_chat));
            linearLayout = ((LinearLayout) itemView.findViewById(R.id.linear_layout));
            profile_image = ((CircleImageView) itemView.findViewById(R.id.chatwith_imageview));
        }
    }

}
