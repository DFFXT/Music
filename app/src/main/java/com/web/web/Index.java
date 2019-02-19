package com.web.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.annotation.JSONField;
import com.web.common.base.BaseActivity;
import com.web.moudle.music.page.local.MusicActivity;
import com.web.moudle.net.retrofit.BaseRetrofit;
import com.web.moudle.songSheetEntry.adapter.JSEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;

@SuppressLint("InlinedApi")
public class Index extends BaseActivity implements OnClickListener,OnTouchListener{
	private String[] name;
	private int[] bgList;
	ArrayList<Button> butList= new ArrayList<>();
	public int getLayoutId(){
		return(R.layout.index);
	}



	@Override
	public void initView() {

		Index.requestWritePermission(this);
		findId();
		makeData();
		fillTextAndColor();
		go();





		/*Retrofit.Builder builder=new Retrofit.Builder()
				.baseUrl("http://59.37.96.220/")
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(new Converter.Factory() {
					@Override
					public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
						return new MConverter<>(type);
					}

					@Override
					public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
						return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
					}
				});
		Retrofit retrofit=builder.build();
		retrofit.create(NetApis.Music.class).s()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(new ObservableTransformer<BaseNetBean, Object>() {
                    @Override
                    public ObservableSource<Object> apply(Observable<BaseNetBean> upstream) {
                        return upstream.map(BaseNetBean::getErrcode);
                    }
                })
                .subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object baseNetBean2) {
                Log.i("log","OK"+baseNetBean2);
            }

            @Override
            public void onError(Throwable e) {
                Log.i("log","eee"+e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
*/

	}

	void go(){
        final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "aim.jar");
        DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                getDir("dex", MODE_PRIVATE).getAbsolutePath(), null, getClassLoader());
        Class libProviderClazz = null;
        try {
            libProviderClazz = cl.loadClass("com.web.web.Text");

            Toast.makeText(this, libProviderClazz.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }



		/*View v=Flutter.createView(this,getLifecycle(),"SingerView");

		setContentView(v);*/

		//Intent intent=new Intent(this,ArtistActivity.class);
		//startActivity(intent);



	}
	public void findId(){
		butList.add(findViewById(R.id.index_c1_b1));
		butList.add(findViewById(R.id.index_c1_b2));
		butList.add(findViewById(R.id.index_c2_b1));
		butList.add(findViewById(R.id.index_c2_b2));
		int len=butList.size();
		for(int i=0;i<len;i++){
			butList.get(i).setOnClickListener(this);
		}
	}
	void fillTextAndColor(){//--填充界面
		for(int i=0;i<Math.min(butList.size(),name.length);i++){
			butList.get(i).setText(name[i]);
			butList.get(i).setBackgroundColor(bgList[i]);
		}
	}
	private Class[] path;
	void makeData(){//--数据源
		name = new String[]{"浏览器","闹钟","音乐","电脑"};

		path = new Class[]{Browser.class,Alarm_Activity.class,
				MusicActivity.class,Computer.class};
		
		bgList=new int[]{cTi(R.color.lightBlue),cTi(R.color.lightBlue),
				cTi(R.color.gray),cTi(R.color.gray)};
		//fontList=new int[]{};
	}
	int cTi(int colorId){return getResources().getColor(colorId);}
	@Override
	public void onClick(View v){
		Intent intent=null;
		int len=butList.size();
		for(int i=0;i<len;i++){
			if(v.getId()==butList.get(i).getId()){
				intent=new Intent(Index.this,path[i]);
			}
		}
		startActivity(intent);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {//--触摸事件
		int len=butList.size();
		for(int i=0;i<len;i++){
			if(v.getId()==butList.get(i).getId()){
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundColor(cTi(R.color.lightDark));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundColor(bgList[i]);
				}
			}
		}
		return false;
	}

	public static void requestWritePermission(Activity activity){
		int code1=ActivityCompat.checkSelfPermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE);
		int code2=ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(code1!= PackageManager.PERMISSION_GRANTED||code2!=PackageManager.PERMISSION_GRANTED)
		ActivityCompat.requestPermissions(activity,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE},
				1);

	}
}

	



