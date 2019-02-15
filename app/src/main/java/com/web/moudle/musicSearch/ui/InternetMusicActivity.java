package com.web.moudle.musicSearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

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
    public static String keyWords;
    private TopBarLayout topBarLayout;
    private CharSequence[] item;

    private void initData() {
        keyWords = getIntent().getStringExtra(KEYWORD);
        item=new CharSequence[]{
                getText(R.string.music),
                getText(R.string.singer_tab),
                getText(R.string.album_tab),
                getText(R.string.songSheet),
                getText(R.string.video)
        };
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
        topBarLayout=findViewById(R.id.topBar);

        TopBarLayout topBarLayout = findViewById(R.id.topBar);
        topBarLayout.setEndImageListener(v -> SearchActivity.actionStart(this, RESULT_CODE_SEARCH));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);


        pageList.add(new MusicFragment());
        pageList.add(new ArtistFragment());
        pageList.add(new AlbumFragment());
        pageList.add(new SheetFragment());
        pageList.add(new VideoFragment());
        Bundle b = new Bundle();
        b.putString(MusicFragment.keyword, keyWords);

        viewPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), pageList));
        viewPager.setOffscreenPageLimit(pageList.size());
        tabLayout.setTabIndicatorFullWidth(false);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        int pageIndex=0;
        for (BaseSearchFragment f : pageList) {
            f.setArguments(b);
            int finalPageIndex = pageIndex;
            f.setSearchCallBack((number)->{
                String num;
                if(number>999) num="999+";
                else num=""+number;
                Objects.requireNonNull(tabLayout.getTabAt(finalPageIndex)).setText(addNumber(item[finalPageIndex],num));
                return null;
            });
            Objects.requireNonNull(tabLayout.getTabAt(pageIndex)).setText(item[pageIndex]);
            pageIndex++;
        }



        setTitle();

    }
    private Spannable addNumber(CharSequence text,String number){
        SpannableString spannable=new SpannableString(text+"("+number+")");
        spannable.setSpan(new TextAppearanceSpan(this,R.style.searchResultNumber),text.length(),spannable.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannable;
    }



    private void setTitle(){
        topBarLayout.setMainTitle(keyWords+"-"+getString(R.string.search));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        if (requestCode == RESULT_CODE_SEARCH) {
            keyWords = data.getStringExtra(SearchActivity.INPUT_DATA);
            setTitle();
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
