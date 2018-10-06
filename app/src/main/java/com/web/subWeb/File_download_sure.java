package com.web.subWeb;

import com.web.service.FileDownloadService;
import com.web.config.GetFiles;
import com.web.web.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class File_download_sure extends Activity implements OnClickListener{  
    Button selectPathButton,downloadOk;
    EditText nameEdit;
    RelativeLayout parentBox;
    String url,name;
    long length;
    GetFiles gfFiles=new GetFiles();
    protected void onCreate(Bundle savedInstanceState) {  
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.browser_download_sure); 
    	init();
    	
    	
    }  
    public void init(){
    	parentBox=(RelativeLayout)findViewById(R.id.parentBox);
    	nameEdit=(EditText)findViewById(R.id.nameEdit);
    	selectPathButton=(Button)findViewById(R.id.selectPathButton);
    	downloadOk=(Button)findViewById(R.id.downloadOk);
    	parentBox.setOnClickListener(this);
    	selectPathButton.setOnClickListener(this);
    	downloadOk.setOnClickListener(this);
    	Intent intent=getIntent();
    	url=intent.getStringExtra("url");
    	name=intent.getStringExtra("name");
    	length=intent.getLongExtra("length", 0);
    	
    	nameEdit.setText(name);
    }
    public void download(){//--下载
    	Intent intent=new Intent(this,FileDownloadService.class);
    	intent.putExtra("url", url);
    	intent.putExtra("name", nameEdit.getText().toString());
    	intent.putExtra("length", length);
    	intent.setAction("download");
    	startService(intent);
    }
    public void show(String str){
    	Toast.makeText(File_download_sure.this, str, Toast.LENGTH_LONG).show();
    }
	@Override
	public void onClick(View v) {//--点击事件
		int Id=v.getId();
		if(Id==R.id.parentBox){
			finish();
		}else if(Id==R.id.selectPathButton){
			
		}else if(Id==R.id.downloadOk){
			download();
			finish();
		}
	}
 
}
