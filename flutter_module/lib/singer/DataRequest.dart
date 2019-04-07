import 'dart:async';

import 'package:http/http.dart' as http;
class DataRequest{

  Future<String> getRowSingerData(String artistId) async{
    http.get(Uri.parse("http://musicmini.qianqian.com/2018/static/singer/person_14413780_0_1.html")).then((res){
      print(res.body);
    });
   /*HttpClient client=HttpClient();
   var request=await client.getUrl();
   var response=await request.close();
   print(response);
   var responseBody = response.transform(UTF8).
    print(responseBody);*/
   return null;
  }
}