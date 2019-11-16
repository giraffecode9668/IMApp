## 前言
上一次的app开发[【四】基于Bmob后端云的一体式app——SeekHelper](https://www.jianshu.com/p/f5b315504453)
中，组长给我安排一个即时通讯聊天的功能，可是我做不出，存在原因有如下：
- 预计的聊天功能开发难度大
- 时间不足
- 数据库设计不合理

然后我一直念念不忘，非常不服，所以挑战实现IM功能，开发了这个项目
通过commit记录我是花了半个月时间的（哭笑），不只做了IM，还做了添加好友，个人设置，登录注册功能

> 项目地址：[https://github.com/giraffecode9668/IMApp](https://github.com/giraffecode9668/IMApp)

## 工具
安卓开发工具：android studio
免费后端云平台：Bmob以及BmobIM
> Bmob2019年12月1日将注销未绑定域名的应用，可能以后不能使用BmobIm实现IM

## 知识
基本的android开发知识

## Bmob
Bmob就像是一个远程数据库(mysql)，所以在开发中把逻辑代码写在app中，实体也在app中，请求数据库操作也在app中，一体式app

![Bmob数据表](https://upload-images.jianshu.io/upload_images/15569173-7f0e4a7aa495b9f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## 界面


![注册](https://upload-images.jianshu.io/upload_images/15569173-f3ba238cf323e7df.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220) |  ![登录](https://upload-images.jianshu.io/upload_images/15569173-fd6ac40c72b04d48.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220)
---- | ----- 
 ![侧边栏](https://upload-images.jianshu.io/upload_images/15569173-9aa6c275a80eca16.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220) | ![主界面](https://upload-images.jianshu.io/upload_images/15569173-0fcf031510f63631.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220)
 ![添加好友](https://upload-images.jianshu.io/upload_images/15569173-77d624b31e82a326.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220) | ![接受添加好友](https://upload-images.jianshu.io/upload_images/15569173-9a4df98902c941f2.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220)
![通讯列表](https://upload-images.jianshu.io/upload_images/15569173-36873765d4e0d404.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220) | ![聊天](https://upload-images.jianshu.io/upload_images/15569173-05e51b526c167a8c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/220)





