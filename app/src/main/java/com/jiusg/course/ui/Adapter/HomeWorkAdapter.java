package com.jiusg.course.ui.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.jiusg.course.R;
import com.jiusg.course.domain.Homework;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/21.
 */
public class HomeWorkAdapter  extends RecyclerView.Adapter<HomeWorkAdapter.ViewHolder>{

    private ArrayList<Homework> homeworks;
    private Context context;

    public HomeWorkAdapter(ArrayList<Homework> homeworks,Context context){
        this.homeworks = homeworks;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(context)
                .inflate(R.layout.item_recyclerview_homework, parent, false)));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(homeworks.get(position).content+"");
        holder.time.setText(homeworks.get(position).time+"");
        holder.className.setText(homeworks.get(position).schedule.getClassNam()+"");
        holder.classTeacher.setText(homeworks.get(position).schedule.getClassTeacher()+"");

        boolean isFinish = homeworks.get(position).isFinish;
        if(isFinish){
            holder.isFinish.setText("已完成");
            holder.isFinish.setClickable(false);
        }else {
            holder.isFinish.setText("未完成");
        }

    }

    @Override
    public int getItemCount() {
        return homeworks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        TextView time;
        TextView className;
        TextView classTeacher;
        TextView isFinish;

        public ViewHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
            className = (TextView) itemView.findViewById(R.id.className);
            classTeacher = (TextView) itemView.findViewById(R.id.classTeacher);
            isFinish = (TextView) itemView.findViewById(R.id.isFinish);

            isFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("改变状态为已完成?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    AVObject object = new AVObject("HomeWork");
                                    object.setObjectId(homeworks.get(getAdapterPosition()).objectId+"");
                                    object.put("isFinish",true);
                                    object.saveInBackground();

                                    isFinish.setText("已完成");
                                }
                            }).setNegativeButton("取消",null)
                            .show();

                }
            });
        }
    }
}
