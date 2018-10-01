### 现有功能：

一. 二维码生成与解析。 支持选择图片文件解析二维码，用的是这个二维码库 [[XQRCode](https://github.com/xuexiangjys/XQRCode)]。

二. Android SDK版本列表查看。最高支持 `Android 9.0（API 27, Pie 派)`，同时还显示当前设备的版本。

三. 应用管理。现在只支持查看应用名称、包名、版本、安装包路径、安装包大小、安装时间、最后更新时间。

四. 新闻盒子。暂不支持登陆，现在支持以下站点及频道：

1. [少数派](https://www.sspai.com/) :官网抓取其api来用，json格式。

- 少数派文章

2. [数字尾巴](http://www.dgtle.com/) :官网抓取其api来用，json格式。

- [数字尾巴鲸闻](http://news.dgtle.com/)

3. [干货集中营](http://gank.io/) :[官方提供api](http://gank.io/api)，json格式。

- 全部

- 安卓

- 福利

4. [RSSHub](https://docs.rsshub.app/) :官方提供api，json格式。

- [V2EX-最新主题](https://www.v2ex.com)

- [豆瓣正在上映的电影](https://movie.douban.com/cinema/nowplaying/guangzhou/)

五. 天气。只支持 **当前IP** 的地名、当前气候、当前气温。

六. 哔哩哔哩。

1. 通过弹幕列表里指定弹幕的加密用户id得知正式用户id。有时可能会得到多个结果。

2. 通过视频vid获取封面图片

七. [Server酱](http://sc.ftqq.com/3.version) :可以推送消息至微信公众号。

八. 应用下载：支持以下站点的应用昵称、版本、更新日期、网页地址、下载地址。

1. GitHub release。通过调用[Github官方API](https://developer.github.com/)来获取GitHub 项目的release 列表，如:[zhihaofans/Android.Box的releases](https://github.com/zhihaofans/Android.Box/releases)。

2. [酷安网v1](https://www.coolapk.com/)。通过对酷安网网页的解析获取应用信息，但是如果该应用被酷安官方屏蔽了网页显示的话就解析失败(只能在客户端看，网页显示404)

3. [Fir.im v1](https://fir.im/)。通过调用[Fir.im官方API](https://fir.im/docs)项目信息，需要API  Token。我保证不会收集API  Token，API  Token仅保存于设备本地，并在卸载后由系统一同删除。

4. [豌豆荚v1](https://www.wandoujia.com/apps)。通过对豌豆荚网页的解析获取应用信息。

