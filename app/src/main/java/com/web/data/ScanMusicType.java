package com.web.data;

import org.litepal.crud.DataSupport;

public class ScanMusicType extends DataSupport {
    private String scanSuffix;
    private int minTime;
    private boolean scanable=true;

    public ScanMusicType(){

    }
    public ScanMusicType(String scanSuffix,int minTime,boolean scanable){
        this.scanSuffix=scanSuffix;
        this.minTime=minTime;
        this.scanable=scanable;
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


    public void setScanSuffix(String scanSuffix) {
        this.scanSuffix = scanSuffix;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }
}
