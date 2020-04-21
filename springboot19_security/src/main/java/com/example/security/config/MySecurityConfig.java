package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

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
        //http.formLogin().loginPage("/userlogin"); // 自定义登陆页面

        // 开启记住我功能
        http.rememberMe(); // 登陆成功之后，将cookie发送给浏览器保存，以后登陆都会带上这个cookie

        // 开启自动配置的注销功能（请求/logout表示用户注销并清空session，注销后会默认回到登陆页面）
        http.logout().logoutSuccessUrl("/"); // 手动指定注销后跳转首页，会删除cookie
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
