package com.web.moudle.musicDownload.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.web.common.base.BaseAdapter;
import com.web.common.base.BaseViewHolder;
import com.web.common.util.ResUtil;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicDetail;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class DownloadViewAdapter extends BaseAdapter<DownloadMusic> {
	private List<DownloadMusic> dataList;
	private Context context;
	private OnItemClickListener listener;
	public DownloadViewAdapter(Context c, List<DownloadMusic> data){
	    super(data);
		dataList=data;
		context=c;
	}

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.download_listview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NotNull BaseViewHolder holder, int position, @Nullable DownloadMusic item) {
	    InternetMusicDetail music=dataList.get(position).getInternetMusicDetail();
        holder.bindText(R.id.musicName,music.getSongName());
        holder.bindText(R.id.hasDownload, ResUtil.getFileSize(music.getHasDownload())+"/");
        holder.bindText(R.id.fullSize, ResUtil.getFileSize(music.getSize()));
        holder.bindImage(R.id.downloadStatu,dataList.get(position).getStatus()==DownloadMusic.DOWNLOAD_DOWNLOADING?R.drawable.icon_play_black :R.drawable.icon_pause_black)
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


	public void setListener(OnItemClickListener listener) {
		this.listener = listener;
	}


    public interface OnItemClickListener{
		void itemClick(View v,int position);
	}

}
