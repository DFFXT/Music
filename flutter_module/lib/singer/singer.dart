import 'package:flutter/material.dart';
import 'dart:core';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:sky_engine/_http/http.dart';
import 'package:flutter_module/singer/SingerWidget.dart';
import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';


class SingerView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "123",
      home: SingerView1(),
    );
  }
}

class SingerView1 extends StatefulWidget {
  @override
  State createState() {
    return SingerState();
  }
}

class SingerState extends State<SingerView1>
    with SingleTickerProviderStateMixin {
  GlobalKey _image = GlobalKey();
  NestedScrollView _scrollView;
  String size = "1x0000000000000";
  SingerTitle _singerTitle = SingerTitle(
    title: "title",
    opacity: 0.0,
  );


  @override
  Widget build(BuildContext context) {
    return _scrollView;
  }

  TabController _tabController;

  int a(){

  }
  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: choices.length, vsync: this);

    _scrollView=NestedScrollView(
      headerSliverBuilder: (buildContext, bool) {
        return [
          SliverAppBar(
            //pinned=true appbar 可视
            pinned: true,
            expandedHeight: 200,
            title: _singerTitle,
            backgroundColor: Color(0xFFFF7963),
            centerTitle: true,
            bottom: new TabBar(
              isScrollable: true,
              tabs: choices.map((Choice choice) {
                return new Tab(
                  text: choice.title,
                );
              }).toList(),
              controller: _tabController,
            ),
            flexibleSpace: GestureDetector(
              child: Container(
                key: _image,
                child: FlexibleSpaceBar(
                  centerTitle: true,
                  background: Image.asset("images/singer_default_icon.png"),
                  collapseMode: CollapseMode.pin,
                ),
              ),
              onTap: () {
                print(_image.currentContext.size);
              },
            ),
          )
        ];
      },
      body: new TabBarView(
        children: choices.map((Choice choice) {
          return new Padding(
            padding: const EdgeInsets.all(16.0),
            child: new ChoiceCard(choice: choice),
          );
        }).toList(),
        controller: _tabController,
      ),
      controller: TrackingScrollController(),
    );

    _scrollView.controller.addListener(() {
      if (_image.currentContext.size.height < 200) {}
      if (_singerTitle.state.mounted) {
        _singerTitle.state.setState(() {
          _singerTitle.state.setProperty(opacity: (1-_image.currentContext.size.height/300.0)*4-1) ;
        });
      }
    });
    getDate();
  }

  @override
  void dispose() {
    super.dispose();
    _tabController.dispose();
  }
  Future<String> getDate() async{
    MethodChannel channel=MethodChannel("io");
    final String date= await channel.invokeMethod("getDate");
    print(date);
    return null;
  }
}

class Choice {
  const Choice({this.title, this.icon});

  final String title;
  final IconData icon;
}

const List<Choice> choices = const <Choice>[
  const Choice(title: 'CAR', icon: Icons.directions_car),
  const Choice(title: 'BICYCLE', icon: Icons.directions_bike),
  const Choice(title: 'BOAT', icon: Icons.directions_boat),
  const Choice(title: 'BUS', icon: Icons.directions_bus),
  const Choice(title: 'TRAIN', icon: Icons.directions_railway),
  const Choice(title: 'WALK', icon: Icons.directions_walk),
];

class ChoiceCard extends StatelessWidget {
  const ChoiceCard({Key key, this.choice}) : super(key: key);

  final Choice choice;

  @override
  Widget build(BuildContext context) {
    final TextStyle textStyle = Theme.of(context).textTheme.display1;
    return new Card(
      color: Colors.white,
      child: new Center(
        child: new Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            new Icon(choice.icon, size: 128.0, color: textStyle.color),
            new Text(choice.title, style: textStyle),
          ],
        ),
      ),
    );
  }
}
