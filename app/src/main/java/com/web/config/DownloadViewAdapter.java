package com.web.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.web.common.base.BaseViewHolder;
import com.web.common.util.StrUtil;
import com.web.data.InternetMusic;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.web.R;

import java.text.DecimalFormat;
import java.util.List;


public class DownloadViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
	private List<DownloadMusic> dataList;
	private Context context;
	private int downloadId;
	private OnItemClickListener listener;
	public DownloadViewAdapter(Context c, List<DownloadMusic> data){
		dataList=data;
		context=c;
	}

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.download_listview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
	    InternetMusic music=dataList.get(position).getInternetMusic();
        holder.bindText(R.id.musicName,music.getMusicName());
        holder.bindText(R.id.hasDownload,StrUtil.getFileSize(music.getHasDownload())+"/");
        holder.bindText(R.id.fullSize,StrUtil.getFileSize(music.getFullSize()));
        holder.bindImage(R.id.downloadStatu,dataList.get(position).getStatus()==DownloadMusic.DOWNLOAD_DOWNLODINF?R.drawable.play:R.drawable.pause)
        .setOnClickListener(v->{
            if(listener!=null){
                listener.itemClick(v,position);
            }
        });
        holder.rootView.findViewById(R.id.close).setOnClickListener(v->{
            if(listener!=null){
                listener.itemClick(v,position);
            }
        });
    }

    @Override
	public long getItemId(int arg0) {
		return arg0;
	}

    @Override
    public int getItemCount() {
        return dataList==null?0:dataList.size();
    }


	public void setListener(OnItemClickListener listener) {
		this.listener = listener;
	}


	public interface OnItemClickListener{
		void itemClick(View v,int position);
	}

}
