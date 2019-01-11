import 'package:flutter/material.dart';

class SingerTitle extends StatefulWidget {
  final _SingerTitleState state = _SingerTitleState();

  SingerTitle({title, opacity}) {
    state.setProperty(title: title,opacity: opacity);
  }

  @override
  State createState() {
    return state;
  }
}

class _SingerTitleState extends State<SingerTitle> {
  String _title = "";
  double _opacity = 1.0;

  void setProperty({opacity,title}){
    if(opacity==null)opacity=_opacity;
    if (opacity < 0.0)
      opacity = 0.0;
    else if (opacity > 1.0) opacity = 1.0;
    if(title==null) title=_title;
    if(this.mounted){
      setState(() {
        _opacity=opacity;
        _title=title;
      });
    }else{
      _opacity=opacity;
      _title=title;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Opacity(
      opacity: _opacity,
      child: Text(_title),
    );
  }
}
