

<div align="center">
<div style="height:256px; width:256px; text-align: center;">
</div>
 <a href="http://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/spring--boot-2.2.5.RELEASE-green.svg" alt="spring-boot">
       </a>
</div>

### 系统介绍
angel-small-tools基于Spring Boot 、java 实现一套工具集的整合，包含常用工具类与一些开发时的可能需要用到的小工具，所有代码都有清晰的中文注释，方便使用者阅读理解。如果需要使用，可以直接打成一个jar包引入到
工程，或者直接copy自己需要的功能代码到项目中

### 项目功能
- 二维码：自定义内容生成二维码图片，解析二维码图片内容
- 短链接：采用id数字策略加混淆算法生产短链接码
- json脱敏：内置用户四要素属性判断，支持各种复杂类型的对象转json，json内容已脱敏
- log脱敏：基于注解的方式使用，对符合内置脱敏字段的对象进行脱敏，标注在方法上，可以打印方法执行前与执行后的log记录
- 操作限流：采用注解设计与guava RateLimiter实现的令牌桶算法限流方式，使用简单，`@ExtRateLimiter(speed=1,timeOut=1000)`标注在方法上即可
- 文件上传：集成上传文件操作，支持上传文件至阿里云、腾讯云、本地机器（内置断点续传实现，支持本地操作）
- 对象操作：简化对象的复制操作，支持集合对象复制，复杂对象复制，采用函数式编程实现复制过程中修改目标对象属性值内容
- http操作：简化http请求操作，可直接通过参数 `(url,param, method, returnType)`实现返回结果转成指定对象
- 签名工具：内置签名参数拼接、字段排序等各种签名方式的方法

#### 未来规划
* 一键配置文件存储服务商  
* 可扩展添加文件存储服务商  
* 支持禁用指定存储服务商   
* 支持动态限流调用次数
* 大文件支持断点续传
* 多文件同时上传
* 项目代码结构精简
* 一次打包，到处运行


### 帮助文档
[阿里云OSS文件操作指南](https://help.aliyun.com/document_detail/84785.html?spm=a2c4g.11186623.6.783.19144d83vTa4Eq)

[腾讯云OCS文件操作指南](https://cloud.tencent.com/document/product/436/35215)

### 关于作者
有问题可以联系我==>[蒋老湿](https://juejin.im/user/5b6a41ef5188251ac858752a/posts)


![](https://user-gold-cdn.xitu.io/2019/12/24/16f3828996afd060)
