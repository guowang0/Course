package com.jiusg.course.ui.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.jiusg.course.R;
import com.jiusg.course.domain.Homework;
import com.jiusg.course.domain.Message;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/21.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private ArrayList<Message> messages;
    private Context context;

    public MessageAdapter(ArrayList<Message> messages, Context context){
        this.messages = messages;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(context)
                .inflate(R.layout.item_recyclerview_message, parent, false)));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(messages.get(position).content+"");
        holder.className.setText(messages.get(position).className+"");
        holder.username.setText(messages.get(position).userName+"");

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        TextView username;
        TextView className;

        public ViewHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.content);
            className = (TextView) itemView.findViewById(R.id.className);
            username = (TextView) itemView.findViewById(R.id.username);

        }
    }
}
