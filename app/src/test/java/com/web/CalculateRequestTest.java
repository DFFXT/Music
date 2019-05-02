package com.web;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.web.moudle.music.player.bean.SongSheetWW;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CalculateRequestTest {

    @Test
    public void f(){
        Object f= new Gson().fromJson("[{\"code\":200,\"coverPath\":\"null\",\"id\":2,\"musicCount\":0,\"name\":\"喜爱\",\"songs\":[],\"userId\":3333},{\"code\":200,\"id\":3,\"musicCount\":0,\"songs\":[],\"userId\":3333},{\"code\":200,\"id\":4,\"musicCount\":0,\"songs\":[],\"userId\":3333},{\"code\":200,\"coverPath\":\"\",\"id\":5,\"musicCount\":0,\"name\":\"sheet-2\",\"songs\":[],\"userId\":3333},{\"code\":200,\"coverPath\":\"\",\"id\":6,\"musicCount\":0,\"name\":\"sheet-2\",\"songs\":[],\"userId\":3333},{\"code\":200,\"coverPath\":\"\",\"id\":7,\"musicCount\":0,\"name\":\"sheet-2\",\"songs\":[],\"userId\":3333},{\"code\":200,\"coverPath\":\"\",\"id\":8,\"musicCount\":0,\"name\":\"sheet-2\",\"songs\":[],\"userId\":3333}]", List.class);
        f.equals("");
    }



}