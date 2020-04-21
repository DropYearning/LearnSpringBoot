#  LearnSpringBoot-SpringBoot热部署

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1 热部署

- 在开发中我们修改一个Java文件后想看到效果不得不重启应用，这导致大量时间花费，我们希望不重启应用的情况下，程序可以自动部署（热部署）。有以下四种情况，如何能实现热部署。
    - 1、模板引擎
        - 在Spring Boot中开发情况下禁用模板引擎的cache ：[禁用thymeleaf模板引擎缓存_Java_Java_Glory的博客-CSDN博客](https://blog.csdn.net/Java_Glory/article/details/89882350)
        - 页面模板改变ctrl+F9可以重新编译当前页面并生效
    - 2、Spring Loaded：Spring官方提供的热部署程序，实现修改类文件的热部署
        - 下载Spring Loaded（项目地址https://github.com/springprojects/spring-loaded） 
        - 添加运行时参数； -javaagent:C:/springloaded-1.2.5.RELEASE.jar –noverify
    - JRebel：收费的一个热部署软件 ， 安装插件使用即可
    - **Spring Boot Devtools**（推荐）

## 2 Spring Boot Devtools

- （1） devtools可以实现页面热部署（即页面修改后会立即生效，这个可以直接在application.properties文件中配置spring.thymeleaf.cache=false来实现），**实现类文件热部署**（类文件修改后不会立即生效），实现对属性文件的热部署。即devtools会监听classpath下的文件变动，并且会立即重启应用（发生在保存时机），注意：因为其采用的虚拟机机制，该项重启是很快的
- （2）配置了true后在修改java文件后也就支持了热启动，不过这种方式是属于项目重启（速度比较快的项目重启），会清空session中的值，也就是如果有用户登陆的话，项目重启后需要重新登陆。默认情况下，/META-INF/maven，/METAINF/resources，/resources，/static，/templates，/public这些文件夹下的文件修改不会使应用重启，但是会重新加载（devtools内嵌了一个LiveReload server，当资源发生改变时，浏览器刷新）。

- 引入Maven依赖:

    ```xml
    <dependency>
    		<groupId>org.springframework.boot</groupId>
    		<artifactId>spring-boot-devtools</artifactId>
    		<optional>true</optional>
    </dependency>
    ```

- 引入之后就支持热部署了，可以按`Ctrl+F9`热部署（Eclipise的快捷键是Ctrl+S）

- 注意：
    - 支持Java代码修改、前端页面修改的热部署

    - 配置文件的不能使用热部署重载

    - 热部署后会清空session中的值

        

## 参考资料

- [SpringBoot配置devtools实现热部署 - LSPZ - 博客园](https://www.cnblogs.com/lspz/p/6832358.html)