package com.web.moudle.musicDownload.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.web.common.base.BaseMultiSelectAdapter;
import com.web.common.base.BaseViewHolder;
import com.web.common.util.ResUtil;
import com.web.config.Shortcut;
import com.web.data.InternetMusicDetail;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.annotation.NonNull;


public class DownloadViewAdapter extends BaseMultiSelectAdapter<DownloadMusic> {
	private Context context;
	private OnItemClickListener itemClickListener;
	private OnItemLongClickListener itemLongClickListener;
	public DownloadViewAdapter(Context c, List<DownloadMusic> data){
	    super(c,data);
		context=c;
	}

    @NonNull
    @Override
    public View onCreateItemView(@NonNull ViewGroup parent, int viewType) {
	    switch (viewType){
	        case DownloadMusic.DOWNLOAD_COMPLETE_HEAD:{
                return LayoutInflater.from(context).inflate(R.layout.view_textview,parent,false);
            }
            case DownloadMusic.DOWNLOAD_DOWNLOADING_HEAD:{
                return LayoutInflater.from(context).inflate(R.layout.view_textview,parent,false);
            }
            case DownloadMusic.DOWNLOAD_COMPLETE:{
                return LayoutInflater.from(context).inflate(R.layout.item_download_complete,parent,false);
            }
            default:{
                return LayoutInflater.from(context).inflate(R.layout.download_listview,parent,false);
            }
        }

    }

    @Override
    public void onBindItemView(@NotNull BaseViewHolder holder, int position, @Nullable DownloadMusic item) {
	    if(item==null)return;
	    InternetMusicDetail music=item.getInternetMusicDetail();
        int status=item.getStatus();
        switch (status) {
            case DownloadMusic.DOWNLOAD_COMPLETE_HEAD: {
                TextView tv =holder.bindText(R.id.textView,ResUtil.getString(R.string.downloadComplete));
                tv.setBackgroundResource(R.color.gray);
                tv.setPadding(20,10,10,10);
            }break;
            case DownloadMusic.DOWNLOAD_DOWNLOADING_HEAD: {
                TextView tv=holder.bindText(R.id.textView,ResUtil.getString(R.string.downloading));
                tv.setBackgroundResource(R.color.gray);
                tv.setPadding(20,10,10,10);
            }break;
            default:{
                holder.bindText(R.id.musicName,music.getSongName());
                if(status==DownloadMusic.DOWNLOAD_COMPLETE){//**构建已完成item
                    if(!Shortcut.isStrictEmpty(music.getAlbumName())){
                        holder.bindText(R.id.tv_albumName, music.getAlbumName()+"  - ");
                    }
                    holder.bindText(R.id.tv_singerName,music.getArtistName());
                    holder.findViewById(R.id.iv_play).setOnClickListener(v->click(v,position));
                    if(position==getItemCount()-1){
                        holder.itemView.setBackgroundResource(R.color.transparent);
                    }else{
                        holder.itemView.setBackgroundResource(R.drawable.bottom_dashline_1px);
                    }
                    holder.itemView.findViewById(R.id.item_parent).setOnClickListener(v->click(v,position));
                }else{//**构建正在下载的item
                    holder.bindText(R.id.hasDownload, ResUtil.getFileSize(music.getHasDownload()));
                    holder.bindText(R.id.fullSize, ResUtil.getFileSize(music.getSize()));
                    holder.bindImage(R.id.downloadStatu,status==DownloadMusic.DOWNLOAD_DOWNLOADING?R.drawable.icon_play_black :R.drawable.icon_pause_black)
                            .setOnClickListener(v-> click(v,position));
                    holder.itemView.setOnClickListener(null);
                    holder.itemView.findViewById(R.id.close).setOnClickListener(v-> click(v,position));
                    if(getData().get(position+1).getStatus()==DownloadMusic.DOWNLOAD_COMPLETE_HEAD){
                        holder.itemView.setBackgroundResource(R.color.transparent);
                    }else{
                        holder.itemView.setBackgroundResource(R.drawable.bottom_dashline_1px);
                    }
                    ((ProgressBar)holder.findViewById(R.id.progress)).setProgress((int)(music.getHasDownload()*100/music.getSize()));
                }
                if(isSelect()){
                    holder.itemView.setOnClickListener(v-> toggleSelect(position));
                }
                holder.itemView.findViewById(R.id.item_parent).setOnLongClickListener(v->{
                    longClick(v,position);
                    return true;
                });
            }
        }



    }


    private void click(View v,int position){
	    if(isSelect()){
	        toggleSelect(position);
	        return;
        }
	    if(itemClickListener==null)return;
	    itemClickListener.itemClick(v,position);
    }
    private void longClick(View v,int position){
	    if(isSelect())return;
        setSelect(true);
	    if(itemLongClickListener==null)return;
	    itemLongClickListener.itemLongClick(v,position);
    }


    public void setItemClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}

    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public int getSelectType(int position) {
	    int type=getViewType(position);
        if(type==DownloadMusic.DOWNLOAD_COMPLETE_HEAD||type==DownloadMusic.DOWNLOAD_DOWNLOADING_HEAD){
            return TYPE_NONE_SELECTOR;
        }else{
            return TYPE_LEFT_SELECTOR;
        }
    }

    @Override
    public int getViewType(int position) {
        return getData().get(position).getStatus();
    }
    public interface OnItemClickListener{
        void itemClick(View v,int position);
    }
    public interface OnItemLongClickListener{
        boolean itemLongClick(View v,int position);
    }
}
