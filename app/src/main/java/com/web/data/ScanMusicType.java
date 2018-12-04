package com.web.data;

import org.litepal.crud.DataSupport;

public class ScanMusicType extends DataSupport {
    private String scanSuffix;
    private int minFileSize;
    private boolean scanable=true;

    public ScanMusicType(){

    }
    public ScanMusicType(String scanSuffix,int minFileSize,boolean scanable){
        this.scanSuffix=scanSuffix;
        this.minFileSize=minFileSize;
        this.scanable=scanable;
    }

    public int getMinFileSize() {
        return minFileSize;
    }

    public boolean isScanable() {
        return scanable;
    }

    public void setScanable(boolean scanable) {
        this.scanable = scanable;
    }

    public String getScanSuffix() {
        return scanSuffix;
    }

    public void setMinFileSize(int minFileSize) {
        this.minFileSize = minFileSize;
    }

    public void setScanSuffix(String scanSuffix) {
        this.scanSuffix = scanSuffix;
    }
}
