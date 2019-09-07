## Tiny Resume: 简历文档智能识别以及数值化子系统
## 设计目的
 - 对Excel, Word, PDF格式简历文件进行文本提取。
 - 对提取的简历文本进行智能识别归档并输出标准JSON格式。
 - 通过微服群的云计算对标准简历文本进行20多种属性的数值化处理。
 - 提供上述服务的相应在线API。
 - 简历文本评价项目策略需由外部提供，并能够实时更新和个别调整。

##原始文档(支持Excel，Word，PDF)解析

###1. 原始文本信息
```json
{
  "contents" : [
      {
      "x" : 1,
      "y" : 0,
      "s" : "氏名"
    },
      {
      "x" : 5,
      "y" : 0,
      "s" : "T.F"
    },
      {
      "x" : 17,
      "y" : 0,
      "s" : "性別"
    },
      {
      "x" : 20,
      "y" : 0,
      "s" : "男"
    },
  ]
}
```


##评价项目的策略

###1. 枚举计算模式(例)
```json
{
  "name" : "Education",
  "values" : [
      {
      "name" : "Junior",
      "value" : 10.0
    },
      {
      "name" : "Vocational",
      "value" : 15.0
    },
      {
      "name" : "University",
      "value" : 30.0
    },
      {
      "name" : "Postgraduate",
      "value" : 40.0
    },
      {
      "name" : "Doctor",
      "value" : 50.0
    },
      {
      "name" : "Postdoctoral",
      "value" : 55.0
    }
  ]
}
```


###2. 线性计算模式(例)
```json
{
  "name" : "Age",
  "expression" : "value / 65.0 * 0.05"
}
```


###3. 自然语言演算模式(微服调用例)
```json
{
  "name" : "Skill",
  "service" : {
    "url" : "http://192.168.100.200/v1/api/dis?id=Skill",
    "timeout" : 1000,
    "auth"    : "true",
    "token"   : "2ae068ce2"
  }
}
```

##评价结果

###1. 各模式演(计)算结果
```json
{
  "name" : "Skill",
  "values" : [
      {
      "value" : "{识别的文本1}",
      "rate" : .9823
    },
      {
      "value" : "{识别的文本2}",
      "rate" : .3814
    }
  ]
}
```

##More Detail, See The Samples

---

Email   : wuweibg@gmail.com
