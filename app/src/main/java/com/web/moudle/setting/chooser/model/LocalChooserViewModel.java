package com.web.moudle.setting.chooser.model;

import android.os.Environment;

import com.web.common.base.ChineseComparator;
import com.web.common.util.ResUtil;
import com.web.moudle.setting.chooser.bean.LocalItem;
import com.web.web.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocalChooserViewModel extends ViewModel {
    private String rootPath= Environment.getExternalStorageDirectory().getAbsolutePath();
    private List<LocalItem> list=new ArrayList<>();
    private List<LocalItem> dirList=new ArrayList<>();
    private List<LocalItem> fileList=new ArrayList<>();
    private MutableLiveData<List<LocalItem>> localItemList=new MutableLiveData<>();
    private MutableLiveData<String> currentPath=new MutableLiveData<>();




    public void requestEntry(String path){
        requestEntry(new File(path));
    }
    public void requestEntry(File rootFile){
        if(rootFile.isFile()){//**错误，不是文件夹
            return;
        }
        list.clear();
        dirList.clear();
        fileList.clear();
        if(!rootPath.equals(rootFile.getAbsolutePath())){
            list.add(new LocalItem(LocalItem.TYPE_BACK,
                    ResUtil.getString(R.string.chooser_backToParent),
                    rootFile.getParent(),
                    "",
                    ""));
        }
        buildList(rootFile.listFiles());
        //**排序
        Collections.sort(dirList,(o1, o2) -> ChineseComparator.INSTANCE.compare(o1.getName(),o2.getName()));
        Collections.sort(fileList,(o1, o2) -> ChineseComparator.INSTANCE.compare(o1.getName(),o2.getName()));
        list.addAll(dirList);
        list.addAll(fileList);
        localItemList.setValue(list);
        currentPath.setValue(rootFile.getAbsolutePath()+File.separator);
    }
    private void buildList(File[] files){
        for(File file:files){
            String startTag;
            int type;
            if(file.isFile()){
                startTag= ResUtil.getFileSize(file.length());
                type=LocalItem.TYPE_FILE;
            }else{
                startTag=file.list().length+"项";
                type=LocalItem.TYPE_DIR;
            }
            LocalItem item=new LocalItem(
                    type,
                    file.getName(),
                    file.getAbsolutePath(),
                    startTag,
                    ResUtil.timeFormat("YYY-MM-DD",file.lastModified())
            );
            if(file.isFile()){
                fileList.add(item);
            }else {
                dirList.add(item);
            }
        }
    }

    public MutableLiveData<List<LocalItem>> getLocalItemList() {
        return localItemList;
    }

    public MutableLiveData<String> getCurrentPath() {
        return currentPath;
    }
}
