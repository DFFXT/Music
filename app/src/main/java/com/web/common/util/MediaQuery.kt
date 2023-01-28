package com.web.common.util

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.web.app.MyApplication
import com.web.common.constant.AppConfig
import com.web.config.Shortcut
import com.web.data.Music
import com.web.data.MusicList
import com.web.moudle.music.player.SongSheetManager
import com.web.moudle.music.player.bean.SongSheet
import com.web.moudle.setting.suffix.SuffixSelectActivity
import com.web.web.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.sourceforge.pinyin4j.PinyinHelper
import org.litepal.crud.DataSupport
import java.io.File
import java.util.*

object MediaQuery {
    /**
     * 从手机曲库扫描
     */
    @JvmStatic
    fun scanMedia(ctx: Context, callback: (isOk: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            AppConfig.noNeedScan = true
            var hasMusic=false
            ctx.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null).use { cursor ->
                if (cursor == null) {
                    launch(Dispatchers.Main) {
                        callback(false)
                    }
                    return@launch
                }
                val types = SuffixSelectActivity.getScanType()
                val out = arrayOfNulls<String>(2)
                while (cursor.moveToNext()) {
                    var index = cursor.getColumnIndex("_data")
                    val path = cursor.getString(index)

                    index = cursor.getColumnIndex("duration")
                    val duration=cursor.getInt(index)

                    for (type in types) {
                        if (!type.isScanable) continue
                        if (path != null && path.toLowerCase().endsWith(type.scanSuffix.toLowerCase()) && duration >= type.minTime) {
                            if(!SuffixSelectActivity.isEnableSystemMusic()&&! path.startsWith(Environment.getExternalStorageDirectory().absolutePath)){
                                continue
                            }

                            hasMusic=true
                            val lastSeparatorChar = path.lastIndexOf(File.separatorChar)
                            //**文件名包含后缀
                            var fileName: String = path
                            if (lastSeparatorChar >= 0) {
                                fileName = path.substring(lastSeparatorChar + 1)
                            }
                            Shortcut.getName(out, fileName)
                            //**去后缀
                            val lastIndex = out[0]!!.lastIndexOf('.')
                            val music = Music(out[0]!!.substring(0, lastIndex), out[1], path)
                            index = cursor.getColumnIndex("_size")
                            val size = cursor.getInt(index)

                            music.size = size.toLong()
                            music.suffix=out[0]!!.substring(lastIndex+1)
                            music.duration = duration
                            index = cursor.getColumnIndex("album_id")
                            music.album_id = cursor.getInt(index).toString()
                            index = cursor.getColumnIndex("album")
                            music.album = cursor.getString(index)

                            val m = DataSupport.where("path=?", music.path).findFirst(Music::class.java)
                            if (m == null) {
                                music.save()
                            } else {//***更新保持groupID不变
                                music.groupId = m.groupId
                                music.id = m.id
                                music.update(m.id.toLong())
                            }
                            break
                        }
                    }
                }
            }
            launch (Dispatchers.Main){
                callback(hasMusic)
            }
        }

    }

    /**
     * 获取本地SQLLite数据
     */
    @JvmStatic
    fun getLocalList(callback: (ArrayList<MusicList<Music>>) -> Unit) {
        GlobalScope.launch (Dispatchers.IO){
            val musicList: ArrayList<MusicList<Music>> = arrayListOf()
            //**获取默认列表的歌曲
            val defList = DataSupport.findAll<Music>(Music::class.java)

            val defGroup = MusicList<Music>(ResUtil.getString(R.string.default_))
            defGroup.addAll(defList)
            for(i in 0 until defGroup.size){
                val firstChar = defGroup[i].musicName[0]
                defGroup[i].firstChar = if(PinYin.isChinese(firstChar)){
                    val res = PinyinHelper.toHanyuPinyinStringArray(firstChar)
                    if(res!=null){
                        res[0].toCharArray()[0]
                    }else{
                        '*'
                    }
                }else if(PinYin.isEnglish(firstChar)){
                    firstChar
                }else{
                    '*'
                }
                defGroup[i].firstChar = defGroup[i].firstChar.toUpperCase()
            }
            musicList.add(defGroup)
            //**获取自定义列表的歌曲

            val sheet=SongSheetManager.getSongSheetList()
            val sheetList = sheet.songList
            if(sheetList.size==0){
                sheet.addSongSheet(SongSheet(ResUtil.getString(R.string.sheet_like)))
            }
            for (songSheet in sheetList) {
                val group = MusicList<Music>(songSheet.name)
                songSheet.each { id ->
                    for (m in defList) {
                        if (m.id == id) {
                            group.add(m)
                        }
                    }
                }
                musicList.add(group)
            }
            launch(Dispatchers.Main) {
                callback(musicList)
            }
        }
    }


    /**
     * 删除音乐，并更新歌单
     */
    @JvmStatic
    fun deleteMusic(ctx: Context,music: Music,group:Int,deleteFile:Boolean){
        if (deleteFile) {//******删除源文件并更新媒体库
            deleteCacheMusic(music)
            ctx.contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_data=?", arrayOf<String>(music.path))
        }


        if (group == 0 || deleteFile) {//**影响group 0,在默认歌单里面删除或者删除源文件
            val list = SongSheetManager.getSongSheetList().songList
            for (songSheet in list) {
                songSheet.remove(music.id)
            }
            music.delete()
        }
        else {
            SongSheetManager.getSongSheetList().songList[group-1].remove(music.id)
            if(group==1){//**喜爱歌单删除
                music.isLike=false
                music.saveOrUpdate()
            }
            SongSheetManager.getSongSheetList().save()
        }
    }

    /**
     * 修改音乐信息 path
     */
    @JvmStatic
    fun updateMusic(oldMusic: Music,newMusic:Music){
        val values=ContentValues()
        values.put("_data",newMusic.path)
        MyApplication.context.contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,values,"_data=?", arrayOf(oldMusic.path))
    }

    /**
     * 删除音乐相关文件
     */
    @JvmStatic
    fun deleteCacheMusic(music: Music){
        var file = File(music.path)
        file.delete()
        file = File(music.lyricsPath)
        file.delete()
        music.delete()
    }


}