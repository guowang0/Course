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
import com.jiusg.course.base.BaseApplication;
import com.jiusg.course.domain.Homework;
import com.jiusg.course.domain.Score;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/21.
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>{

    private ArrayList<Score> scores;
    private Context context;

    public ScoreAdapter(ArrayList<Score> scores, Context context){
        this.scores = scores;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(context)
                .inflate(R.layout.item_recyclerview_score, parent, false)));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.classNam.setText(scores.get(position).classNum+"");
        holder.score.setText(scores.get(position).score+"分");
        if(null != BaseApplication.user)
        holder.username.setText(BaseApplication.user.getString("name")+"");
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView classNam;
        TextView score;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);

            classNam = (TextView) itemView.findViewById(R.id.className);
            score = (TextView) itemView.findViewById(R.id.score);
            username = (TextView) itemView.findViewById(R.id.username);

        }
    }
}
