package com.web.moudle.musicSearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;

import com.web.common.base.BaseActivity;
import com.web.common.base.BaseFragmentPagerAdapter;
import com.web.misc.TopBarLayout;
import com.web.moudle.search.SearchActivity;
import com.web.web.R;

import java.util.ArrayList;
import java.util.Objects;

public class InternetMusicActivity extends BaseActivity {
    private final static int RESULT_CODE_SEARCH = 1;
    public final static String KEYWORD = "keyword";

    private ArrayList<BaseSearchFragment> pageList = new ArrayList<>();
    private ViewPager viewPager;
    private String keyWords;


    private void initData() {
        keyWords = getIntent().getStringExtra(KEYWORD);

    }

    @Override
    public int getLayoutId() {
        return R.layout.music_internet;
    }

    @Override
    public void initView() {
        initData();
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        TopBarLayout topBarLayout = findViewById(R.id.topBar);
        topBarLayout.setEndImageListener(v -> SearchActivity.actionStart(this, RESULT_CODE_SEARCH));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);


        pageList.add(new MusicFragment());
        pageList.add(new ArtistFragment());
        pageList.add(new AlbumFragment());
        pageList.add(new SheetFragment());
        Bundle b = new Bundle();
        b.putString(MusicFragment.keyword, keyWords);
        for (BaseSearchFragment f : pageList) {
            f.setArguments(b);
        }
        viewPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), pageList));
        viewPager.setOffscreenPageLimit(pageList.size());
        tabLayout.setTabIndicatorFullWidth(false);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getText(R.string.music));
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(getText(R.string.singer_tab));
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText(getText(R.string.album_tab));
        Objects.requireNonNull(tabLayout.getTabAt(3)).setText(getText(R.string.songSheet));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        if (requestCode == RESULT_CODE_SEARCH) {
            keyWords = data.getStringExtra(SearchActivity.INPUT_DATA);
            viewPager.setCurrentItem(0);
            for (int i = 0; i < pageList.size(); i++) {
                pageList.get(i).search(keyWords);
            }
        }
    }

    public static void actionStart(Context ctx, String word) {
        Intent intent = new Intent(ctx, InternetMusicActivity.class);
        intent.putExtra(KEYWORD, word);
        ctx.startActivity(intent);
    }
}
