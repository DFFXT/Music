package com.web.data;

import java.util.ArrayList;
import java.util.List;

public class MusicList<T> {
    private List<T> music=new ArrayList<>();
    private String title;
    public MusicList(String title){
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int size(){
        return music.size();
    }
    public T get(int index){
        return music.get(index);
    }
    public int indexOf(T o){
        return music.indexOf(o);
    }
    public void add(T object){
        music.add(object);
    }
    public void add(int index,T object){
        music.add(index,object);
    }
    public void addAll(List<T> list){
        music.addAll(list);
    }
    public List<T> getAll(){return music;}
    public T remove(int index){
        return music.remove(index);
    }
    public boolean remove(T t){
        return music.remove(t);
    }
    public void clear(){
        music.clear();
    }
    public List<T> getMusicList(){
        return music;
    }
}
