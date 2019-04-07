import 'dart:convert';

import 'package:http/http.dart' as http;

import 'RecommendBean.dart';


class RecommendRequest{
  
  request(void Function(RecommendBean details) callback) async{
    var map=Map<String,String>();
    map.putIfAbsent("cuid", (){return "C08F3FE0D20BC1C506E601E6367BFD54";});
    map.putIfAbsent("deviceid", (){return "863254010121571";});
    map.putIfAbsent("BDUSS", (){return "123";});//**这里BDUSS不能为某一个值否则返回只有部分信息
    http.get("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=7.0.1.1&"
        "channel=1413b&operator=0&method=baidu.ting.plaza.newIndex&cuid=C08F3FE0D20BC1C506E601E6367BFD54&",headers: map)
        .then((res){
        var obj=RecommendBean.fromJson(json.decode(res.body));
        print(obj.toString());
        callback(obj);
    });
  }
}
