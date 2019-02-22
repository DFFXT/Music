class RecommendBean {
  int errorCode;
  List<Modules> modules;

  RecommendBean({this.errorCode, this.modules});

  RecommendBean.fromJson(Map<String, dynamic> json) {
    errorCode = json['error_code'];
    if (json['modules'] != null) {
      modules = new List<Modules>();
      json['modules'].forEach((v) {
        modules.add(new Modules.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['error_code'] = this.errorCode;
    if (this.modules != null) {
      data['modules'] = this.modules.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Modules {
  String titleMoreJump;
  String reason;
  String moduleId;
  String id;
  List<Result> result;
  String linkurl;
  String style;
  int nums;
  String styleNums;
  String moduleid;
  String title;
  String titleMore;
  String picurl;
  String jumpType;
  String styleId;
  String stylePicBili;

  Modules(
      {this.titleMoreJump,
        this.reason,
        this.moduleId,
        this.id,
        this.result,
        this.linkurl,
        this.style,
        this.nums,
        this.styleNums,
        this.moduleid,
        this.title,
        this.titleMore,
        this.picurl,
        this.jumpType,
        this.styleId,
        this.stylePicBili});

  Modules.fromJson(Map<String, dynamic> json) {
    titleMoreJump = json['title_more_jump'];
    reason = json['reason'];
    moduleId = json['module_id'];
    id = json['id'];
    if (json['result'] != null) {
      result = new List<Result>();
      json['result'].forEach((v) {
        result.add(new Result.fromJson(v));
      });
    }
    linkurl = json['linkurl'];
    style = json['style'];
    nums = json['nums'];
    styleNums = json['style_nums'];
    moduleid = json['moduleid'];
    title = json['title'];
    titleMore = json['title_more'];
    picurl = json['picurl'];
    jumpType = json['jump_type'];
    styleId = json['style_id'];
    stylePicBili = json['style_pic_bili'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['title_more_jump'] = this.titleMoreJump;
    data['reason'] = this.reason;
    data['module_id'] = this.moduleId;
    data['id'] = this.id;
    if (this.result != null) {
      data['result'] = this.result.map((v) => v.toJson()).toList();
    }
    data['linkurl'] = this.linkurl;
    data['style'] = this.style;
    data['nums'] = this.nums;
    data['style_nums'] = this.styleNums;
    data['moduleid'] = this.moduleid;
    data['title'] = this.title;
    data['title_more'] = this.titleMore;
    data['picurl'] = this.picurl;
    data['jump_type'] = this.jumpType;
    data['style_id'] = this.styleId;
    data['style_pic_bili'] = this.stylePicBili;
    return data;
  }
}

class Result {
  String method;
  String conId;
  String conTitle;
  String jump;
  String picUrl;
  String author;
  String songId;

  Result(
      {this.method,
        this.conId,
        this.conTitle,
        this.jump,
        this.picUrl,
        this.author});

  Result.fromJson(Map<String, dynamic> json) {
    method = json['method'];
    conId = json['con_id'];
    conTitle = json['con_title'];
    jump = json['jump'];
    picUrl = json['pic_url'];
    if(picUrl==null){
      picUrl=json['pic_radio'];
    }
    author = json['author'];
    songId=json['song_id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['method'] = this.method;
    data['con_id'] = this.conId;
    data['con_title'] = this.conTitle;
    data['jump'] = this.jump;
    data['pic_url'] = this.picUrl;
    data['author'] = this.author;
    return data;
  }
}
