package com.web.moudle.music.page.local;

import android.animation.ValueAnimator;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.web.common.base.BaseActivity;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.common.util.help.ViewExtKt;
import com.web.data.IgnoreMusic;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.misc.BasePopupWindow;
import com.web.misc.DrawableItemDecoration;
import com.web.misc.InputItem;
import com.web.misc.ToolsBar;
import com.web.moudle.music.page.BaseMusicPage;
import com.web.moudle.music.page.local.control.adapter.IndexBarAdapter;
import com.web.moudle.music.page.local.control.adapter.LocalMusicAdapter;
import com.web.moudle.music.page.local.control.ui.SheetCreateAlert;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.music.player.SongSheetManager;
import com.web.moudle.music.player.bean.SongSheet;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MusicListPage extends BaseMusicPage {
    public final static String pageName = ResUtil.getString(R.string.page_local);
    private MusicList<Music> data;
    private RecyclerView rv_musicList;
    private LocalMusicAdapter adapter;
    private MusicPlay.Connect connect;
    private int groupIndex = 0;
    private ToolsBar toolsBar;
    private View iv_add;


    /**
     * 默认组子项长点击
     *
     * @param position p
     */
    private void defaultGroupChildLongClick(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.default_child_long_click);
        Music music = connect.getMusic(groupIndex,position);
        if (IgnoreMusic.isIgnoreMusic(music)){
            popupMenu.getMenu().findItem(R.id.ignore).setTitle(R.string.autoPlayDisableCancel);
        }else{
            popupMenu.getMenu().findItem(R.id.ignore).setTitle(R.string.autoPlayDisable);
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.musicPlay) {//**播放
                this.connect.musicSelect(groupIndex, position);
            } else if (id == R.id.delete) {//**删除
                connect.delete(false, groupIndex, position);
            } else if (id == R.id.deleteOrigin) {//**完全删除
                if (groupIndex == 0) {//**group 0 才可以删除源文件
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle(ResUtil.getString(R.string.deleteOrigin))
                            .setMessage(data.get(position).getPath())
                            .setNegativeButton(ResUtil.getString(R.string.no), null)
                            .setPositiveButton(ResUtil.getString(R.string.yes), (dialog, witch) -> {
                                connect.delete(true, groupIndex, position);
                            })
                            .create()
                            .show();
                }
            } else if (id == R.id.setAsLiske) {//喜欢
                addToList(data.get(position).getId());
            }else if (id == R.id.ignore){ //禁止自动播放
                if (IgnoreMusic.isIgnoreMusic(music)){
                    IgnoreMusic.createIgnoreMusic(music).delete();
                }else{
                    IgnoreMusic.createIgnoreMusic(music).saveOrUpdate();
                }
                adapter.notifyItemChanged(position);
            } else if (id == R.id.detailInfo) {//**详细信息
                showDetail(data.get(position), position);
            } else if (id == R.id.multiSelect) {//**多选
                showMultiSelect(view);
                adapter.select(position);
            }
            return false;
        });
    }


    private void addToList(int... musicIds) {
        List<SongSheet> list = SongSheetManager.INSTANCE.getSongSheetList().getSongList();
        ArrayList<String> sheetNameList = new ArrayList<>();
        for (SongSheet sheet : list) {
            sheetNameList.add(sheet.getName());
        }
        if (sheetNameList.size() != 0) {
            sheetNameList.remove(0);
        }
        SheetCreateAlert alert = new SheetCreateAlert(requireContext(), ResUtil.getString(R.string.songSheet));
        alert.setList(sheetNameList);
        alert.setCreateListener(() -> {
            String name = "sheet-" + SongSheetManager.INSTANCE.getSongSheetList().getSongList().size();

            SongSheetManager.INSTANCE.createNewSongSheet(name);
            sheetNameList.add(sheetNameList.size(), name);
            alert.setList(sheetNameList);
            alert.getAdapter().notifyItemRangeInserted(sheetNameList.size() - 1, 1);
            connect.groupChange();
            return null;
        });
        alert.setItemClickListener((v, index) -> {
            for (int id : musicIds) {
                list.get(index + 1).add(id);
            }
            connect.groupChange();
            SongSheetManager.INSTANCE.getSongSheetList().save();
            alert.dismiss();
            return null;
        });
        alert.show(rv_musicList);
    }

    /**
     * 显示音乐信息
     * 修改音乐信息
     *
     * @param music music
     */
    private void showDetail(Music music, int index) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_music_detail, null);
        BasePopupWindow popupWindow = new BasePopupWindow(rv_musicList.getContext(), v);

        TextView tv_abPath = v.findViewById(R.id.tv_abPath);
        tv_abPath.setText(music.getPath());

        ((TextView) v.findViewById(R.id.tv_duration)).setText(ResUtil.timeFormat("mm:ss", music.getDuration()));
        ((TextView) v.findViewById(R.id.tv_size)).setText(ResUtil.getFileSize(music.getSize()));

        InputItem ii_name = v.findViewById(R.id.layout_musicName);
        ii_name.setText(music.getMusicName());
        ii_name.setListenerSave(text -> {
            if (!text.equals(music.getMusicName()) && music.rename(text, music.getSinger())) {
                adapter.notifyItemChanged(index);
                tv_abPath.setText(music.getPath());
                //**修改的是当前音乐需要重新load
                if (music == connect.getConfig().getMusic()) {
                    connect.dispatchLoad();
                }
            }
            return music.getMusicName();
        });

        InputItem ii_artist = v.findViewById(R.id.layout_artistName);
        ii_artist.setText(music.getSinger());
        ii_artist.setListenerSave(text -> {
            if (!text.equals(music.getSinger()) && music.rename(music.getMusicName(), text)) {
                adapter.notifyItemChanged(index);
                //**修改的是当前音乐需要重新load
                if (music == connect.getConfig().getMusic()) {
                    connect.dispatchLoad();
                }
                tv_abPath.setText(music.getPath());
            }
            return music.getSinger();
        });
        popupWindow.show(rv_musicList);

    }

    /**
     * 显示多选
     *
     * @param v view
     */
    private void showMultiSelect(View v) {
        adapter.setSelect(true);
        showToolBar();
    }

    private void showToolBar() {
        if (toolsBar == null) {
            toolsBar = new ToolsBar((BaseActivity) requireActivity());
            toolsBar.addItem(0, R.string.remove)
                    .addItem(1, R.string.deleteOrigin)
                    .addItem(2, R.string.addToGroup)
                    .addItem(3, R.string.selectAll)
                    .setBackClick(() -> {
                        adapter.setSelect(false);
                        return null;
                    });
            toolsBar.setItemClick((id) -> {
                switch (id) {
                    case 0: {
                        connect.delete(false, groupIndex, adapter.getSelectList((music, index) -> index));
                        adapter.getSelectSet().clear();
                    }
                    break;
                    case 1: {
                        connect.delete(true, groupIndex, adapter.getSelectList((music, index) -> index));
                        adapter.getSelectSet().clear();
                    }
                    break;
                    case 2: {
                        List<Integer> list = adapter.getSelectList((music, index) -> music.getId());
                        int[] arr = new int[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            arr[i] = list.get(i);
                        }
                        addToList(arr);
                    }
                    break;
                    case 3: {
                        adapter.setSelectAll(!adapter.isSelectAll());
                    }
                    break;
                }
                return null;
            });
        }
        toolsBar.show();
    }


    /**
     * 设置连接接口
     *
     * @param connect connect
     */
    @Override
    public void setConnect(@NonNull IBinder connect) {
        if (this.connect == null) {
            this.connect = (MusicPlay.Connect) connect;
            this.connect.selectList(groupIndex, -1);
        }

    }

    @NotNull
    @Override
    public String getTitle() {
        return ResUtil.getString(R.string.page_local);
    }

    @Override
    public void setTitle(@NotNull String textView) {
        String sheetName = " - ";
        if (groupIndex == 0) {
            sheetName += ResUtil.getString(R.string.default_);
        } else {
            sheetName += SongSheetManager.INSTANCE.getSongSheetList().getSongList().get(groupIndex - 1).getName();
        }
        sheetName += " ···";
        String title = ResUtil.getString(R.string.page_local) + sheetName;

        CharSequence realTitle = ResUtil.getSpannable(title, sheetName, ResUtil.getColor(R.color.gray), ResUtil.getSize(R.dimen.textSize_min));
        MusicActivity activity = ((MusicActivity) getActivity());
        if (activity != null) {
            activity.getTitleView().setText(realTitle);
        }

    }

    /**
     * 设置主音乐页面的数据
     *
     * @param data data
     */
    void setData(int groupIndex, int child, MusicList<Music> data) {
        this.groupIndex = groupIndex;
        this.data = data;
        if (adapter != null) {
            adapter.setIndex(child);
            adapter.notifyItemChanged(child);
            adapter.update(data);
        }

    }


    void loadMusic(int musicGroupIndex, int position) {
        if (adapter != null && musicGroupIndex == groupIndex) {
            adapter.setIndex(position);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.music_list;
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initView(@NotNull View rootView) {
        iv_add = rootView.findViewById(R.id.iv_add);
        rv_musicList = rootView.findViewById(R.id.musicExpandableList);
        RecyclerView indexBar = rootView.findViewById(R.id.indexBar_musicList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false);
        rv_musicList.setLayoutManager(layoutManager);
        rv_musicList.addItemDecoration(new DrawableItemDecoration(0, 0, 0, 2, RecyclerView.VERTICAL, ResUtil.getDrawable(R.drawable.recycler_divider)));
        if (data != null) {
            adapter = new LocalMusicAdapter(rootView.getContext(), data);
        } else {
            adapter = new LocalMusicAdapter(rootView.getContext(), null);
        }
        adapter.setSelect(false);

        adapter.setItemClickListener((v, position) -> {
            connect.musicSelect(groupIndex, position);
            return null;
        });
        adapter.setAddListener((v, position) -> {
            connect.addToWait(data.get(position), true);
            addAnimation(v);
            return null;
        });

        adapter.setItemLongClickListener((v, position) -> {
            defaultGroupChildLongClick(v, position);
            return true;
        });

        adapter.setToggleLike((music, index) -> {
            if (music.isLike()) {
                SongSheetManager.INSTANCE.removeLike(music);
            } else {
                SongSheetManager.INSTANCE.setAsLike(music);
            }
            connect.groupChange();
            adapter.notifyItemChanged(index);
            return null;
        });
        rv_musicList.setAdapter(adapter);
        if (connect != null) {
            connect.selectList(groupIndex, -1);
        }
        List<Character> indexList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (indexList.size() == 0) {
                indexList.add(data.get(0).getFirstChar());
            } else if (data.get(i).getFirstChar() != indexList.get(indexList.size() - 1)) {
                indexList.add(data.get(i).getFirstChar());
            }
        }
        IndexBarAdapter indexBarAdapter = new IndexBarAdapter();
        indexBar.setAdapter(indexBarAdapter);
        indexBarAdapter.update(indexList);
        rv_musicList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int p = layoutManager.findFirstVisibleItemPosition();
                if (p < 0) return;
                char code = data.get(p).getFirstChar();
                indexBarAdapter.setSelectChar(code);
            }
        });
        rv_musicList.setFocusable(true);
        ViewExtKt.setOnItemClickListener(indexBar, (position, item) -> {
            int index = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getFirstChar() == (Character) item) {
                    index = i;
                    break;
                }
            }
            LinearLayoutManager manager = ((LinearLayoutManager) rv_musicList.getLayoutManager());
            if (manager != null) {
                manager.scrollToPositionWithOffset(index, 0);
            }
            return null;
        });


    }

    private int[] pos = new int[2];


    private ValueAnimator animator;

    private void addAnimation(View view) {
        if (animator != null) {
            animator.cancel();
        }
        view.getLocationOnScreen(pos);
        iv_add.setX(pos[0]);
        iv_add.setY(pos[1]);
        iv_add.setVisibility(View.VISIBLE);
        int dis = ViewUtil.screenHeight() - pos[1];
        int a = 10;
        float time = (float) Math.sqrt(2f * dis / a);
        float vx = ViewUtil.dpToPx(40) / time;
        animator = ValueAnimator.ofFloat(0, time);
        animator.setDuration(600);
        animator.addUpdateListener(ani -> {
            float t = (float) ani.getAnimatedValue();
            iv_add.setY(pos[1] + 0.5f * a * t * t);
            iv_add.setX(pos[0] - vx * t);
        });
        animator.start();
    }

}
