#  LearnSpringBoot-SpringBoot整合-SpringBoot与安全

[SpringBoot_权威教程__哔哩哔哩 (゜-゜)つロ 干杯~-bilibili](https://www.bilibili.com/video/BV1Et411Y7tQ?p=4)



## 1 安全
- Spring Security是针对Spring项目的安全框架，也是Spring Boot底层安全模块默认的技术选型。他可以实现强大的web安全控制。对于安全控制，我们仅需引入`spring-boot-starter-security`模块，进行少量的配置，即可实现强大的安全管理。
- 应用程序的两个主要区域是“**认证**”和“**授权**”（或者访问控制）。这两个主要区域是Spring Security 的两个目标。
- **“认证”（Authentication）**，是建立一个他声明的主体的过程（一个“主体”一般是指用户，设备或一些可以在你的应用程序中执行动作的其他系统）。
- **“授权”（Authorization）** 指确定一个主体是否允许在你的应用程序执行一个动作的过程。为了抵达需要授权的点，主体的身份已经有认证过程建立。
- 这个概念是通用的而不只在Spring Security中。

## 2 SpringBoot整合Spring Security的实验

## 2.1 未引入安全控制时
- ![D89hmRw](https://i.imgur.com/D89hmRw.png)
- 每一级别中的武功秘籍都可以访问到，不鉴定访问权限

## 2.2 引入Spring Security
- 1、添加Maven中Spring Security的依赖`spring-boot-starter-security`
- 2、编写Spring Security的配置类MySecurityConfig继承自WebSecurityConfigurerAdapter，在其中编写授权规则和认证规则：
    - 默认`/login`为登陆页面,若登陆失败会自动重定向到`/login?error`页面
        ```java
        @EnableWebSecurity
        public class MySecurityConfig extends WebSecurityConfigurerAdapter {
            // 定义授权规则
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                // 定制请求的授权规则
                http.authorizeRequests().antMatchers("/").permitAll() // 首页均可访问
                        .antMatchers("/level1/**").hasRole("VIP1")  // 访问level1的武功秘籍需要VIP1身份
                        .antMatchers("/level2/**").hasRole("VIP2")  // 访问level2的武功秘籍需要VIP2身份
                        .antMatchers("/level3/**").hasRole("VIP3"); // 访问level1的武功秘籍需要VIP3身份
                // 开启自动配置的登陆功能(默认/login为登陆页面,若登陆失败会自动重定向到/login?error页面)
                http.formLogin(); // 开启登陆后，如果没有权限就会自动跳转到登陆页面
            }
        
            // 设置不加密
            @Bean
            public static NoOpPasswordEncoder passwordEncoder() {
                return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
            }
        
            // 定义认认证规则，可以配置全局登陆信息（在这里只将信息保存在了内存中，实际业务应该与数据库交互）
            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                // Spring Security 5.x后需要增加.passwordEncoder(new BCryptPasswordEncoder()).设置加密方式。这里使用不加密方式。
                auth.inMemoryAuthentication().withUser("zhangsan").password("123456").roles("VIP1", "VIP2")
                        .and()
                        .withUser("lisi").password("123456").roles("VIP2", "VIP3")
                        .and()
                        .withUser("wangwu").password("123456").roles("VIP1", "VIP3");
        
            }
        }
        ```
        
    > `There is no PasswordEncoder mapped for the id "null"`错误是因为为Spring security 5.0中新增了多种加密方式，必须指定加密方式
    
    > 这里使用了Spring Security不推荐的注入NoOpPasswordEncoder Bean的方式免除密码加密

- 效果：
    - 访问失败，要求登陆：![sVTDvPb](https://i.imgur.com/sVTDvPb.png)
    - 访问不在自己授权下的页面：显示403Forbidden：![HV6S1f3](https://i.imgur.com/HV6S1f3.png)

## 2.3 引入注销功能
```java
    // 定义授权规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定制请求的授权规则
        http.authorizeRequests().antMatchers("/").permitAll() // 首页均可访问
                .antMatchers("/level1/**").hasRole("VIP1")  // 访问level1的武功秘籍需要VIP1身份
                .antMatchers("/level2/**").hasRole("VIP2")  // 访问level2的武功秘籍需要VIP2身份
                .antMatchers("/level3/**").hasRole("VIP3"); // 访问level1的武功秘籍需要VIP3身份
        // 开启自动配置的登陆功能(默认/login为登陆页面,若登陆失败会自动重定向到/login?error页面)
        http.formLogin(); // 开启登陆后，如果没有权限就会自动跳转到登陆页面

        // 开启自动配置的注销功能（请求/logout表示用户注销并清空session，注销后会默认回到登陆页面）
        http.logout().logoutSuccessUrl("/"); // 手动指定注销后跳转首页
    }
```
- 请求/logout表示用户注销并清空session，注销后会默认回到登陆页面

### 2.4 登陆后差异化显示首页
- 借助thymeleaf模版引擎，需要引入SpringSecurity和thymeleaf的整合模块：`thymeleaf-extras-springsecurity5`
    ```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org"
    	  xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity5">
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>武林秘籍管理系统</title>
    </head>
    <body>
    <h1 align="center">欢迎光临武林秘籍管理系统</h1>
    <!--未认证时显示请登录-->
    <div sec:authorize="!isAuthenticated()">
    	<h2 align="center">游客您好，如果想查看武林秘籍 <a th:href="@{/login}">请登录</a></h2>
    </div>
    <!--认证之后显示注销按钮-->
    <div sec:authorize="isAuthenticated()">
    	<h2>
    		<span sec:authentication="name"></span>你好，您的角色有:
    		<span sec:authentication="principal.authorities"></span>
    	</h2>
    	<form th:action="@{/logout}" method="post">
    		<input type="submit" value="注销">
    	</form>
    </div>
    
    <hr>
    <!--判断用户是否有某个角色，并决定是否显示相应的链接-->
    <div sec:authorize="hasRole('VIP1')">
    	<h3>普通武功秘籍</h3>
    	<ul>
    		<li><a th:href="@{/level1/1}">罗汉拳</a></li>
    		<li><a th:href="@{/level1/2}">武当长拳</a></li>
    		<li><a th:href="@{/level1/3}">全真剑法</a></li>
    	</ul>
    </div>
    
    <div sec:authorize="hasRole('VIP2')">
    	<h3>高级武功秘籍</h3>
    	<ul>
    		<li><a th:href="@{/level2/1}">太极拳</a></li>
    		<li><a th:href="@{/level2/2}">七伤拳</a></li>
    		<li><a th:href="@{/level2/3}">梯云纵</a></li>
    	</ul>
    </div>
    
    <div sec:authorize="hasRole('VIP3')">
    	<h3>绝世武功秘籍</h3>
    	<ul>
    		<li><a th:href="@{/level3/1}">葵花宝典</a></li>
    		<li><a th:href="@{/level3/2}">龟派气功</a></li>
    		<li><a th:href="@{/level3/3}">独孤九剑</a></li>
    	</ul>
    </div>
    
    </body>
    </html>
    ```
## 2.5 添加"记住我"的功能
- 只需登陆一次，之后自动登录
- 登陆成功之后，将cookie发送给浏览器保存，以后登陆都会带上这个cookie
    ```java
     @Override
        protected void configure(HttpSecurity http) throws Exception {
            // 定制请求的授权规则
            http.authorizeRequests().antMatchers("/").permitAll() // 首页均可访问
                    .antMatchers("/level1/**").hasRole("VIP1")  // 访问level1的武功秘籍需要VIP1身份
                    .antMatchers("/level2/**").hasRole("VIP2")  // 访问level2的武功秘籍需要VIP2身份
                    .antMatchers("/level3/**").hasRole("VIP3"); // 访问level1的武功秘籍需要VIP3身份
            // 开启自动配置的登陆功能(默认/login为登陆页面,若登陆失败会自动重定向到/login?error页面)
            http.formLogin(); // 开启登陆后，如果没有权限就会自动跳转到登陆页面
            //http.formLogin().loginPage("/userlogin"); // 自定义登陆页面
    
            // 开启记住我功能
            http.rememberMe(); // 登陆成功之后，将cookie发送给浏览器保存，以后登陆都会带上这个cookie
    
            // 开启自动配置的注销功能（请求/logout表示用户注销并清空session，注销后会默认回到登陆页面）
            http.logout().logoutSuccessUrl("/"); // 手动指定注销后跳转首页，会删除cookie
        }
    ```
- 效果：![DMlvJXe](https://i.imgur.com/DMlvJXe.png)
     - 点击注销后cookie会被清除
     

## 2.6 自定义登陆页面
- 默认post形式的`/login`代表处理登陆请求
- 自定义的登陆页也可以开启remember me




## 参考资料
- [Spring Security 无法登陆，报错：There is no PasswordEncoder mapped for the id “null”_Java_Canon_in_D_Major的博客-CSDN博客](https://blog.csdn.net/Canon_in_D_Major/article/details/79675033)
- [Spring Security 报There is no PasswordEncoder mapped for the id "null" - 简书](https://www.jianshu.com/p/9e7792d767b2)
- [Thymeleaf整合Spring Security，解析sec标签没有效果_Java_qq_41700374的博客-CSDN博客](https://blog.csdn.net/qq_41700374/article/details/87010053)
