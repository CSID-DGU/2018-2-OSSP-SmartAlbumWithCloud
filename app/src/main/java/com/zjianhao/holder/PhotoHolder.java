package com.zjianhao.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.zjianhao.R;

/**
 * Created by 张建浩（Clarence) on 2016-6-24 22:40.
 * the author's website:http://www.zjianhao.cn
 * the author's github: https://github.com/zhangjianhao
 */
public class PhotoHolder extends RecyclerView.ViewHolder {
    public TextView date;
    public GridView photoGrid;
    public PhotoHolder(View itemView) {
        super(itemView);
        date = (TextView) itemView.findViewById(R.id.photo_date);
        photoGrid = (GridView)itemView.findViewById(R.id.photo_grid);


    }
}
