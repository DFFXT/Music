package com.web.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.web.data.InternetMusic;

@SuppressLint("SimpleDateFormat") public class GetFiles {
	public static File url=Environment.getExternalStorageDirectory();
	public File[] files=url.listFiles();
	public static String rootPath=url.toString()+"/.0/";
	public static String appPath=url.toString()+"/.0/app/";
	public static String cachePath=rootPath+"cache/";
	public static String logPath=rootPath+"log.txt";
	public static String singerPath=cachePath+"singer/";
	public static String krcPath=cachePath+"lyrics/";
	public long kb=0;
	private Date date;
	private SimpleDateFormat DateFormat = new SimpleDateFormat("HH:mm:ss");
	public ArrayList<String> name=new ArrayList<String>();
	public ArrayList<String> path=new ArrayList<String>();
	public ArrayList<String> size=new ArrayList<String>();
	public DecimalFormat format=new DecimalFormat("0.00");
	public GetFiles(){//--构建文件目录
		createF(rootPath);
		createF(appPath);
		createF(cachePath);
		createF(singerPath);
		createF(krcPath);
	}
	private void createF(String fString){//--不存在就新建
		File file=new File(fString);
		if(!file.exists()) file.mkdirs();
	}
	public void getfiles(File[] files,List<String> suffix,List<Integer> len){//---读取文件信息 [文件树，后缀，读取长度[负数无限制]]
		if(files!=null){
			for(File file_D:files){
				if(file_D.isFile()){
					for(int i=0;i<suffix.size();i++){//---判断后缀
						if(file_D.getName().endsWith(suffix.get(i))&&file_D.length()>=len.get(i)){
							name.add(file_D.getName());
							path.add(file_D.getAbsolutePath());
							size.add(file_D.length()+"");//--获取文件的大小MB
							break;
						}
					}
				}
				else{//---递归
					getfiles(file_D.listFiles(),suffix,len);
				}
			}
		}
	}
	public void getfiles(List<String> suffix, List<Integer> len){
		getfiles(files,suffix, len);
	}
	public void clearEmptyFolder(File[] files,File file){//--清除空文件夹和空文件
		if(files!=null){
			for(File file_T:files){//--无循环及为空//---不能删除 【空->空】 文件夹
				if(file_T.isDirectory()){
					if(file_T.listFiles().length==0){//---空文件jia
						file_T.delete();
						continue;
					}
					clearEmptyFolder(file_T.listFiles(),file_T);
				}
				if(file_T.length()==0&&file_T.isFile()){//--空文件
					file_T.delete();
				}
			}
		}
	}
	public boolean isFileExists(String path){
		File file=new File(path);
		if(!file.exists()) return false;
		return true;
	}
	@JavascriptInterface
	public String readText(String file){//---读取文本文件
		String text="";
		try {
			InputStream fInputStream=new FileInputStream(file);
			Reader reader=new InputStreamReader(fInputStream);
			int tmpchar;
			while((tmpchar=reader.read())!=-1){
				text+=(char)tmpchar;
			}
			reader.close();
		} catch (Exception e) {
			date=new Date(System.currentTimeMillis());
			String daString="读取文件："+DateFormat.format(date);
			write(rootPath+"log.txt", daString+e+"\r\n\r\n", true);
			return "";
		}
		return text;
	}
	@Nullable
	public String readNetData(String url) throws IOException {//--需要新线程
		StringBuilder builder=new StringBuilder();
		URL Url=new URL(url);
		InputStream stream = Url.openConnection().getInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(stream));
		String tmp;
		while((tmp=bReader.readLine())!=null){
			builder.append(tmp);
		}
		return builder.toString();
	}
	public boolean NetDataToLocal(String url,String path){//--需要新线程  保存至本地
		byte[] byte1= new byte[1024];
		try {
			URL Url=new URL(url);
			InputStream stream = Url.openConnection().getInputStream();
			OutputStream oStream = new BufferedOutputStream(new FileOutputStream(path));
			int tmp=0;
			long all=0;
			while((tmp=stream.read(byte1,0,1024))!=-1){
				oStream.write(byte1,0,tmp);
				all+=tmp;
				kb=all;
			}
			oStream.close();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void append(byte[] b){
		try{
			RandomAccessFile file=new RandomAccessFile(rootPath+"log.txt", "rw");
				file.seek(file.length()-1);
				file.write(b);
			file.close();
		}catch(Exception e){
			write( rootPath+"log.txt", "测试"+e, true);
		}
	}
	@JavascriptInterface
	public boolean write(String path,String text,boolean buer){//----文件写入
		try {
			File FILE=new File(path);
			FileWriter fWriter=new FileWriter(FILE,buer);//-------buer为是否向文件追加
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			bWriter.write(text);
			bWriter.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public void error(String e){
		write(rootPath+"log.txt", e+"\r\n\r\n", true);
	}
	
public void UnZip(String ZipFile,String UnZipPath){//--UnZipPath带 '/'
		try {
			File filePath = new File(UnZipPath);
			if(!filePath.exists()){//--解压路径是否存在
				filePath.mkdirs();
			}
			ZipFile file=new ZipFile(ZipFile);
			@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> zlist=(Enumeration<ZipEntry>) file.entries();
			ZipEntry zEntry=null;
			while(zlist.hasMoreElements()){
				zEntry=(ZipEntry) zlist.nextElement();
				if(zEntry.isDirectory()){
					File folder=new File(UnZipPath+zEntry.getName());
					folder.mkdirs();
					continue;
				}
				OutputStream stream = new BufferedOutputStream(new FileOutputStream(UnZipPath+zEntry.getName()));
				InputStream iStream= new BufferedInputStream(file.getInputStream(zEntry));
				int len=0;
				byte[] byte1=new byte[1024];
				while((len=iStream.read(byte1, 0, 1024))!=-1){
					stream.write(byte1,0,len);
				}
				iStream.close();
				stream.close();
			}
			file.close();
		} catch (Exception e) {
			date=new Date(System.currentTimeMillis());
			String daString="解压缩："+DateFormat.format(date);
			write(rootPath+"log.txt", daString+e+"\r\n\r\n", true);
			e.printStackTrace();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
