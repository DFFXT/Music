import 'package:flutter/material.dart';
/*class MText extends Text{
  MText(String data) : super(data){

  }


}*/




Text MText(String text,{double size,color,weight}){
  var fontSize=16.0;
  var textColor=Colors.black;
  var fontWight=FontWeight.normal;
  if(size!=null)fontSize=size;
  if(color!=null)textColor=color;
  if(weight!=null)fontWight=weight;
  return Text(
    text,
    overflow: TextOverflow.ellipsis,
    style: TextStyle(
        decoration: TextDecoration.none,
        color: textColor,
        fontSize: fontSize,
        fontWeight: fontWight,
    ) ,
  );
}
