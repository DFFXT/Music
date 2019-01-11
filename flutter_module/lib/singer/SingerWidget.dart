import 'package:flutter/material.dart';

class SingerTitle extends StatefulWidget {
  final _SingerTitleState state = _SingerTitleState();

  SingerTitle({title = "", opacity = 1.0}) {
    if (opacity < 0.0)
      opacity = 0.0;
    else if (opacity > 1.0) opacity = 1.0;
    state.opacity = opacity;
    state.title = title;
  }

  @override
  State createState() {
    return state;
  }
}

class _SingerTitleState extends State<SingerTitle> {
  String title = "";
  double opacity = 1.0;

  @override
  Widget build(BuildContext context) {
    return Opacity(
      opacity: opacity,
      child: Text(title),
    );
  }
}
