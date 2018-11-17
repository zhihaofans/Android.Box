package com.zhihaofans.androidbox.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.List;
import com.zhihaofans.androidbox.R;

/**
 * RecyclerViewAdapter
 *
 * @author linzhiyong
 * @time 2017年2月8日15:17:41
 * @email wflinzhiyong@163.com
 * @desc
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;

    private List<Integer> data;

    public RecyclerViewAdapter(Context context, List<Integer> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recyclerview, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.imageView.setImageResource(this.data.get(position));
        if (position % 2 == 0) {
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        } else {
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }


}
