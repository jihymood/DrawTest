package com.example.hasee.drawtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.ViewModel;

import java.util.List;

/**
 * Created by HASEE on 2017/8/9 17:18
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ViewModel> list;
    private Context context;


    public void setList(List<ViewModel> list) {
        this.list = list;
    }

    public void addList(List<ViewModel> modelList) {
        list.addAll(modelList);
    }

    public void notifyDataChanged(List<ViewModel> modelList) {
        setList(modelList);
        notifyDataSetChanged();
    }

    public interface onItemClickListener {
        void onItemClick();
    }

    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RecyclerViewAdapter(List<ViewModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_item, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewModel poPoListModel = list.get(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
//        viewHolder.viewBtn.setText("ccccccc");
//        viewHolder.viewBtn.setText(poListModel.toString());
//        viewHolder.viewBtn.setBackgroundResource(R.mipmap.ic_launcher);
        viewHolder.imageView.setImageBitmap(poPoListModel.getBitmap(poPoListModel.getPic()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
//        return list.size() == 0 ? 0 : list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(View view) {
            super(view);
        }

        ImageView imageView;
    }
}
