package com.web.subWeb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.web.common.util.ViewUtil;
import com.web.data.ScanMusicType;
import com.web.common.util.BaseActivity;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.List;

@SuppressLint("InlinedApi")
public class Settings extends BaseActivity {
	private LinearLayout suffixBox;
	String suffix[]=new String[]{"mp3","acc","ogg","wma","wav","mid"};
	public int getLayoutId(){
		return (R.layout.settings);

	}

	@Override
	public void initView() {
		ViewUtil.transparentStatusBar(getWindow());
		findId();
		render();
	}

	void findId(){
		suffixBox=findViewById(R.id.suffixBox);
	}
	void render(){//--java页面补充
		List<ScanMusicType> type= DataSupport.findAll(ScanMusicType.class);
		if(type.size()!=suffix.length){//**重置数据
			DataSupport.deleteAll(ScanMusicType.class);
			for(String suffix_:suffix){
				ScanMusicType scanMusicType=new ScanMusicType();
				scanMusicType.setScanSuffix(suffix_);
				scanMusicType.setMinFileSize(1024*50);
				scanMusicType.setScanable(true);
				scanMusicType.save();
			}
			type=DataSupport.findAll(ScanMusicType.class);
		}
		DecimalFormat format=new DecimalFormat("0.00");
		for(ScanMusicType scan:type){//*************生成选择框，EditText 并绑定事件
			View v= LayoutInflater.from(this).inflate(R.layout.suffixe_item,suffixBox,false);
			CheckBox checkBox=v.findViewById(R.id.suffixItem);
			checkBox.setText(scan.getScanSuffix());
			checkBox.setChecked(scan.isScanable());
			checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
				scan.setScanable(isChecked);
				scan.saveOrUpdate("scanSuffix=?",scan.getScanSuffix());
            });
			EditText editText=v.findViewById(R.id.fileSize);
			editText.setText(format.format(scan.getMinFileSize()/1024.0/1024));
			editText.setOnFocusChangeListener((v12, hasFocus) -> {
				int newSize=(int)(Float.parseFloat(editText.getText().toString())*1024*1024);
				if(newSize!=scan.getMinFileSize()){
					scan.setMinFileSize(newSize);
					scan.save();
				}
            });
			editText.setOnEditorActionListener((v1, actionId, event) -> {
				int newSize=(int)(Float.parseFloat(editText.getText().toString())*1024*1024);
				if(newSize!=scan.getMinFileSize()){
					scan.setMinFileSize(newSize);
					scan.save();
				}
				return false;
			});
			suffixBox.addView(v);
			findViewById(R.id.parent).setOnClickListener((view)->{
				if(getCurrentFocus()!=null){
					getCurrentFocus().clearFocus();
					closeKeyBord(view);
				}
			});
		}
	}
	private void closeKeyBord(View v){//--close键盘
		InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
	
}
