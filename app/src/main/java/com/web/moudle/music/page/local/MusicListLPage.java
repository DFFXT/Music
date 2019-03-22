package com.web.moudle.music.page.local;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.web.common.base.BaseActivity;
import com.web.common.util.PinYin;
import com.web.common.util.ResUtil;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.misc.BasePopupWindow;
import com.web.misc.DrawableItemDecoration;
import com.web.misc.IndexBar;
import com.web.misc.InputItem;
import com.web.misc.ToolsBar;
import com.web.moudle.music.page.BaseMusicPage;
import com.web.moudle.music.page.local.control.adapter.LocalMusicAdapter;
import com.web.moudle.music.page.local.control.ui.SingleTextListAlert;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.music.player.SongSheetManager;
import com.web.moudle.music.player.bean.SongSheet;
import com.web.web.R;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicListLPage extends BaseMusicPage {
    public final static String pageName="MusicList";
    private MusicList<Music> data;
    private RecyclerView rv_musicList;
    private IndexBar indexBar;
    private LocalMusicAdapter adapter;
    private MusicPlay.Connect connect;
    private int groupIndex=0;
    private Drawable arrowDown;
    private ToolsBar toolsBar;




    /**
     * 默认组子项长点击
     * @param position p
     */
    private void defaultGroupChildLongClick(View view,int position){
        PopupMenu popupMenu =new PopupMenu(Objects.requireNonNull(getContext()),view);
        popupMenu.inflate(R.menu.default_child_long_click);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.musicPlay:{//**播放
                    this.connect.musicSelect(groupIndex,position);
                }break;
                case R.id.delete:{//**删除
                    connect.delete(false,groupIndex,position);
                }break;
                case R.id.deleteOrigin:{//**完全删除
                    if(groupIndex==0){//**group 0 才可以删除源文件
                        new android.app.AlertDialog.Builder(getContext())
                                .setTitle(ResUtil.getString(R.string.deleteOrigin))
                                .setMessage(data.get(position).getPath())
                                .setNegativeButton(ResUtil.getString(R.string.no),null)
                                .setPositiveButton(ResUtil.getString(R.string.yes),(dialog,witch)->{
                                    connect.delete(true,groupIndex,position);
                                })
                                .create()
                                .show();
                    }
                }break;
                case R.id.setAsLiske:{
                    setAsLike(data.get(position).getId());
                }break;
                case R.id.detailInfo:{//**详细信息
                    showDetail(data.get(position),position);
                }break;
                case R.id.multiSelect:{//**多选
                    showMultiSelect(view);
                    adapter.select(position);
                }break;
            }
            return false;
        });
    }


    private void setAsLike(int...musicIds){
        List<SongSheet> list=SongSheetManager.INSTANCE.getSongSheetList().getSongList();
        ArrayList<String> sheetNameList=new ArrayList<>();
        for(SongSheet sheet:list){
            sheetNameList.add(sheet.getName());
        }
        sheetNameList.add("");
        SingleTextListAlert alert=new SingleTextListAlert(getContext(),ResUtil.getString(R.string.songSheet));
        alert.setList(sheetNameList);
        alert.setItemClickListener(index->{
            if(index!=sheetNameList.size()-1){
                for(int id:musicIds){
                    list.get(index).add(id);
                }
                connect.groupChange();
                SongSheetManager.INSTANCE.getSongSheetList().save();
                alert.dismiss();
            }else {
                String name="sheet-"+SongSheetManager.INSTANCE.getSongSheetList().getSongList().size();
                sheetNameList.add(sheetNameList.size()-1,name);
                SongSheetManager.INSTANCE.createNewSongSheet(name);
                alert.setList(sheetNameList);
                alert.getAdapter().notifyItemRangeInserted(index,1);
                alert.getAdapter().notifyItemRangeChanged(index,2);
                connect.groupChange();
            }
            return null;
        });
        alert.show(rv_musicList);
    }

    /**
     * 显示音乐信息
     * 修改音乐信息
     * @param music music
     */
    private void showDetail(Music music,int index){
        View v= LayoutInflater.from(getContext()).inflate(R.layout.layout_music_detail,null);
        BasePopupWindow popupWindow=new BasePopupWindow(rv_musicList.getContext(),v);

        TextView tv_abPath=v.findViewById(R.id.tv_abPath);
        tv_abPath.setText(music.getPath());

        ((TextView)v.findViewById(R.id.tv_duration)).setText(ResUtil.timeFormat("mm:ss",music.getDuration()));
        ((TextView)v.findViewById(R.id.tv_size)).setText(ResUtil.getFileSize(music.getDuration()));

        InputItem ii_name=v.findViewById(R.id.layout_musicName);
        ii_name.setText(music.getMusicName());
        ii_name.setListenerSave(text->{
            if(!text.equals(music.getMusicName())&&music.rename(text,music.getSinger())){
                adapter.notifyItemChanged(index);
                tv_abPath.setText(music.getPath());
            }
            return music.getMusicName();
        });

        InputItem ii_artist=v.findViewById(R.id.layout_artistName);
        ii_artist.setText(music.getSinger());
        ii_artist.setListenerSave(text->{
            if(!text.equals(music.getSinger())&&music.rename(music.getMusicName(),text)){
                adapter.notifyItemChanged(index);
                tv_abPath.setText(music.getPath());
            }
            return music.getSinger();
        });
        popupWindow.show(rv_musicList);

    }

    /**
     * 显示多选
     * @param v view
     */
    private void showMultiSelect(View v){
        adapter.setSelect(true);
        createToolsBar();
        toolsBar.show();
    }
    private void createToolsBar(){
        if(toolsBar==null){
            toolsBar=new ToolsBar((BaseActivity) getActivity());
            toolsBar.addItem(0,R.string.remove)
                    .addItem(1,R.string.deleteOrigin)
                    .addItem(2,R.string.addToGroup)
                    .addItem(3,R.string.selectAll)
                    .setBackClick(()->{
                        adapter.setSelect(false);
                        return null;
                    });
            toolsBar.setItemClick((id)->{
                switch (id){
                    case 0:{
                        connect.delete(false,groupIndex, adapter.getSelectList((music,index) -> index));
                        adapter.getSelectSet().clear();
                    }break;
                    case 1:{
                        connect.delete(true,groupIndex, adapter.getSelectList((music,index) -> index));
                        adapter.getSelectSet().clear();
                    }break;
                    case 2:{
                        List<Integer> list=adapter.getSelectList((music,index)-> music.getId());
                        int arr[]=new int[list.size()];
                        for(int i=0;i<list.size();i++){
                            arr[i]=list.get(i);
                        }
                        setAsLike(arr);
                    }break;
                    case 3:{
                        adapter.setSelectAll(!adapter.isSelectAll());
                    }break;
                }
                return null;
            });
        }
    }


    /**
     * 设置连接接口
     * @param connect connect
     */
    @Override
    public void setConnect(@NonNull MusicPlay.Connect connect) {
        if(this.connect==null){
            this.connect = connect;
            connect.getList(groupIndex);
        }

    }

    @NotNull
    @Override
    public String getPageName() {
        return pageName;
    }

    @Override
    public void setTitle(@NotNull TextView textView) {
        textView.setText(ResUtil.getString(R.string.page_local));
        textView.setCompoundDrawables(null, null, arrowDown, null);
        textView.setCompoundDrawableTintMode(PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * 设置主音乐页面的数据
     * @param data data
     */
    void setData(int groupIndex,int child,MusicList<Music> data) {
        this.groupIndex=groupIndex;
        this.data=data;
        if(adapter!=null){
            adapter.setIndex(child);
            adapter.notifyItemChanged(child);
            adapter.setData(data.getMusicList());
            adapter.notifyDataSetChanged();
        }

    }


    void loadMusic(int musicGroupIndex, int position){
        if(adapter!=null&&musicGroupIndex==groupIndex) {
            adapter.setIndex(position);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.music_list;
    }


    @Override
    public void viewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        arrowDown = new BitmapDrawable(getResources(), ResUtil.getBitmapRotate(R.drawable.icon_back_black, -90));
        arrowDown.setBounds(0, 0, 50, 50);
        arrowDown.setTint(Color.WHITE);
    }

    @Override
    public void initView(@NotNull View rootView) {
        rv_musicList =rootView.findViewById(R.id.musicExpandableList);
        indexBar=rootView.findViewById(R.id.indexBar_musicList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(rootView.getContext(),RecyclerView.VERTICAL,false);
        rv_musicList.setLayoutManager(layoutManager);
        rv_musicList.addItemDecoration(new DrawableItemDecoration(0,0,0,50,LinearLayout.VERTICAL,ResUtil.getDrawable(R.drawable.recycler_divider)));
        if(data!=null){
            adapter=new LocalMusicAdapter(rootView.getContext(),data.getMusicList());
        }else {
            adapter=new LocalMusicAdapter(rootView.getContext(),null);
        }
        adapter.setSelect(false);

        adapter.setItemClickListener((v,position)->{
            connect.musicSelect(groupIndex,position);
            return null;
        });
        adapter.setAddListener(position->{
            connect.addToWait(data.get(position));
            return null;
        });

        adapter.setItemLongClickListener((v,position)->{
            defaultGroupChildLongClick(v,position);
            return true;
        });
        rv_musicList.setAdapter(adapter);
        if(connect!=null){
            connect.getList(groupIndex);
        }
        String str="A B C D E F G H I J K L M N O P Q R S T U V W X Y Z *";
        List<String> indexList=Arrays.asList(str.split(" "));
        indexBar.setVerticalGap(10);
        indexBar.setIndexList(indexList);
        rv_musicList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int p=layoutManager.findFirstVisibleItemPosition();
                if(p<0)return;
                char firstLetter=data.get(p).getMusicName().charAt(0);
                char code;
                if(PinYin.isChinese(firstLetter)){//**判断是否是中文，只有中文才会有返回值否则返回null
                    String res[]=PinyinHelper.toHanyuPinyinStringArray(data.get(p).getMusicName().charAt(0));
                    if(res!=null){
                        code= res[0].toCharArray()[0];
                    }else code='*';
                }else{
                    code=firstLetter;
                }

                for(int i=0;i<indexList.size();i++){
                    if((code+"").equalsIgnoreCase(indexList.get(i).charAt(0)+"")){
                        indexBar.setSelectedIndex(i);
                        return;
                    }
                }
                indexBar.setSelectedIndex(str.length()-1);
            }
        });
        rv_musicList.setFocusable(true);


    }

}
