import 'package:flutter/material.dart';
import 'dart:ui';
import 'singer/singer.dart';
void main() => runApp(run(window.defaultRouteName));


StatelessWidget run(String routeName){
  switch(routeName){
    case "singerView":{
      return SingerView();
    }
  }
  return null;
}


