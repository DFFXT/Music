import 'package:flutter/material.dart';
import 'dart:ui';
import 'singer/singer.dart';
import 'package:flutter_module/recommend/Recommend.dart';
//import 'package:flutter/rendering.dart' show debugPaintSizeEnabled;

void main() {
  //debugPaintSizeEnabled = true;
  runApp(run(window.defaultRouteName));
}


StatelessWidget run(String routeName) {
  switch (routeName) {
    case "SingerView":
      {
        return SingerView();
      }
    case "RecommendView":
      {
        return Recommend();
      }
    default :
      {
        return Recommend();
      }
  }
}

