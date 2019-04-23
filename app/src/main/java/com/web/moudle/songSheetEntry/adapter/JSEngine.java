package com.web.moudle.songSheetEntry.adapter;

import android.util.Log;

import com.web.common.base.MyApplication;
import com.web.moudle.songSheetEntry.bean.SongSheetRequestParams;
import com.web.web.Index;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *  JS解析封装
 */
public class JSEngine{

    /** Java执行js的方法 */
    private static final String JAVA_CALL_JS_FUNCTION = "function Test(){ return 1+2; }";

    /** js调用Java中的方法 */
    private static final String JS_CALL_JAVA_FUNCTION = //
            "var ScriptAPI = java.lang.Class.forName(\"" + JSEngine.class.getName()+ "\", true, javaLoader);" + //
                    "var methodRead = ScriptAPI.getMethod(\"jsCallJava\", [java.lang.String]);" + //
                    "function jsCallJava(url) {return methodRead.invoke(null, url);}" + //
                    "function Test(){ return jsCallJava(); }";


    private int pageSize=25;
    //**获取请求地址：通过js计算 得到加密后的url参数
    public SongSheetRequestParams getSongSheetInfo(String sheetId, int page) throws IOException {
        Reader reader=new InputStreamReader(MyApplication.getContext().getAssets().open("encrypt.js"));
        String res=runScript(reader,"getSheetInfo",new Object[]{sheetId,pageSize*page,pageSize});
        String[] arr=res.split("\\?");
        return new SongSheetRequestParams(arr[0],arr[1],arr[2]);
    }

    public void s(){

        String res= null;
        try {
            Reader reader=new InputStreamReader(MyApplication.getContext().getAssets().open("encrypt.js"));
            res = runScript(reader,"getCommentInfo",new Object[]{"606149060",0,30});
            String[] arr=res.split("\\?");
            Log.i("log",res);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            if (result instanceof String) {
                return (String) result;
            } else if (result instanceof NativeJavaObject) {
                return (String) ((NativeJavaObject) result).getDefaultValue(String.class);
            } else if (result instanceof NativeObject) {
                return (String) ((NativeObject) result).getDefaultValue(String.class);
            }
            return result.toString();//(String) function.call(rhino, scope, scope, functionParams);
        } finally {
            Context.exit();
        }
    }

    public static String jsCallJava(String url) {
        return "农民伯伯 encrypt.js call Java Rhino";
    }
}