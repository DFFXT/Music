

import 'package:flutter/material.dart';
import 'package:flutter_module/recommend/Text.dart';
import 'package:flutter/services.dart';
import 'RecommendRequest.dart';
import 'RecommendBean.dart';

class Recommend extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: RecommendView(),
      theme: ThemeData(
          textTheme: TextTheme(
              body1: TextStyle(fontSize: 16, decoration: TextDecoration.none))),
    );
  }
}

class RecommendView extends StatefulWidget {
  @override
  State createState() {
    return RecommendViewState();
  }
}

class RecommendViewState extends State<RecommendView> {
  List<Widget> sheet = new List();
  MethodChannel channel = MethodChannel("recommend/io");

  @override
  void initState() {
    super.initState();
    RecommendRequest().request((bean) {
      setState(() {
        createR(bean);
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: EdgeInsets.only(left: 10, right: 10),
        decoration: BoxDecoration(color: Colors.white),
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: <Widget>[
              Row(
                children: <Widget>[
                  GestureDetector(
                    child: Container(
                      child: MText("今日推荐",size: 20,weight: FontWeight.bold),
                      padding: EdgeInsets.all(10)
                    ),
                    onTapUp: (d) {
                      channel.invokeMethod("todayRecommend");
                    },
                  ),
                  GestureDetector(
                    child: Container(
                      child: MText("榜单",size: 20,weight: FontWeight.bold),
                      padding: EdgeInsets.all(10)
                    ),
                    onTapUp: (d) {
                      channel.invokeMethod("billboard");
                    },
                  )
                ],
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.start,
                mainAxisSize: MainAxisSize.max,
                textDirection: TextDirection.ltr,
              ),
              Container(
                child: Column(
                  children: sheet,
                  crossAxisAlignment: CrossAxisAlignment.start,
                ),
              )
            ],
          ),
          scrollDirection: Axis.vertical,
        ));
  }


  Widget createBigSong(Modules m,void Function() click){
    print(m.picurl);
    return GestureDetector(
      onTapUp: (d){
        click();
      },
      child: Container(
        color: Colors.deepOrange[300],
        padding: EdgeInsets.all(10),
        child: Row(
          children: <Widget>[
            Column(
              children: <Widget>[
                SizedBox(height: 10,),
                MText(m.title,color: Colors.white,size: 24),
                SizedBox(height: 10,),
                MText(m.reason,color: Colors.white)
              ],
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
            ),
            Image.network(m.result[0].picUrl,width: 120,height: 120),
          ],
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          crossAxisAlignment: CrossAxisAlignment.start,
        ),
      ),
    );
  }


  Widget createSheetWidget(Result sheetInfo, {void Function(Result res) click}) {
    return GestureDetector(
      onTapUp: (d) {
        if (click != null) click(sheetInfo);
      },
      child: Container(
        padding: EdgeInsets.only(bottom: 5, top: 5),
        //**Container如果没有color，点击事件在空白区域不响应
        color: Colors.transparent,
        child: Row(
          children: <Widget>[
            Image.network(
              sheetInfo.picUrl,
              width: 50,
              height: 50,
            ),
            Padding(
              padding: EdgeInsets.all(10),
              child: MText(sheetInfo.conTitle),
            )
          ],
        ),
      ),
    );
  }

  Widget createHotSinger(Result singer,{void Function() click}){
    return GestureDetector(
      onTapUp: (d){
        if(click!=null) click();
      },
      child: Padding(
        padding: EdgeInsets.only(right: 20),
        child: Column(
          children: <Widget>[
            Image.network(singer.picUrl,width:60,height: 60),
            MText(singer.conTitle)
          ],
        ),
      ),
    );
  }

  Widget createHotSingerRow(Modules m){
    List<Widget> _list=List();
    m.result.forEach((e){
      _list.add(createHotSinger(e,click: (){
        channel.invokeMethod("actionStart_SingerEntryActivity",e.conId);
      }));
    });
    return Row(
      children: _list,
    );
  }

  createR(RecommendBean r) {
    for (int i = 0; i < r.modules.length; i++) {
      if(r.modules[i].moduleId == "140"){
        sheet.add(createBigSong(r.modules[i],(){
          channel.invokeMethod("actionStart_AlbumEntryActivity", r.modules[i].result[0].conId);
        }));
      }
      else if (r.modules[i].moduleId == "139") {//**热门歌单
        sheet.add(MText(r.modules[i].title));
        r.modules[i].result.forEach((e) {
          sheet.add(createSheetWidget(e, click: (e) {
            channel.invokeMethod("actionStart_SongSheetActivity", e.conId);
          }));
        });
      } else if (r.modules[i].moduleId == "138") {//**热门专辑
        sheet.add(MText(r.modules[i].title));
        r.modules[i].result.forEach((e) {
          sheet.add(createSheetWidget(e, click: (e) {
            channel.invokeMethod("actionStart_AlbumEntryActivity", e.conId);
          }));
        });
      }else if(r.modules[i].moduleId == "137"){//**热门歌手
        sheet.add(MText(r.modules[i].title));
        sheet.add(createHotSingerRow(r.modules[i]));
      }else if(r.modules[i].moduleId == "135"){
        sheet.add(createBigSong(r.modules[i],(){
          channel.invokeMethod("actionStart_NetMusicListActivity",
              netMusicIntent(r.modules[i].reason, r.modules[i].result[0].conId));
        }));
      }
    }
  }

  //**跳转netMusicListActivity参数
  String netMusicIntent(title,id)=>{
    '"title"':'"$title"',
    '"id"':'"$id"'
  }.toString();
}

