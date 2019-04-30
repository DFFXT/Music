package com.web.moudle.songSheetEntry.adapter;

import com.web.common.base.MyApplication;
import com.web.moudle.songSheetEntry.bean.SongSheetRequestParams;
import com.web.web.Index;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *  JS解析封装
 */
public class JSEngine{

    private Context rhino=Context.enter();
    private Scriptable scope;
    private static JSEngine jsEngine;

    private JSEngine(){}
    //**并发？？
    public static JSEngine getInstance(){
        return new JSEngine();
    }


    private int pageSize=25;
    //**获取请求地址：通过js计算 得到加密后的url参数
    public SongSheetRequestParams getSongSheetInfo(String sheetId, int page) throws IOException {
        Reader reader=new InputStreamReader(MyApplication.getContext().getAssets().open("encrypt.js"));
        String res=runScript(reader,"getSheetInfo",new Object[]{sheetId,pageSize*page,pageSize});
        String[] arr=res.split("\\?");
        reader.close();
        return new SongSheetRequestParams(arr[0],arr[1],arr[2]);
    }

    public SongSheetRequestParams getComment(String songId, int offset,int pageSize,int type) throws IOException {
        Reader reader=new InputStreamReader(MyApplication.getContext().getAssets().open("encrypt.js"));
        String res=runScript(reader,"getCommentListByType",new Object[]{songId,offset,pageSize,type});
        String[] arr=res.split("\\?");
        reader.close();

        return new SongSheetRequestParams(arr[0],arr[1],arr[2]);
    }

    public SongSheetRequestParams getSongSheetTypeParam(String tag,int offset,int pageSize) throws IOException {

        Reader reader=new InputStreamReader(MyApplication.getContext().getAssets().open("encrypt.js"));
        String res=runScript(reader,"getSongSheetType",new Object[]{tag,offset,pageSize});
        String[] arr=res.split("\\?");
        reader.close();

        return new SongSheetRequestParams(arr[0],arr[1],arr[2]);
    }

    private String run(String functionName,Object[] params){
        return ((Function)scope.get("getSongSheetType",scope))
                .call(rhino,scope,scope,params).toString();
    }

    private String runScript(Reader reader, String functionName, Object[] functionParams) throws IOException {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();

            ScriptableObject.putProperty(scope, "javaContext", Context.javaToJS(this, scope));
            ScriptableObject.putProperty(scope, "javaLoader", Context.javaToJS(Index.class.getClassLoader(), scope));


            rhino.evaluateReader(scope, reader, "JSEngine", 1, null);

            Function function = (Function) scope.get(functionName, scope);

            Object result = function.call(rhino, scope, scope, functionParams);
            return result.toString();//(String) function.call(rhino, scope, scope, functionParams);
        } finally {
            Context.exit();
        }
    }

}