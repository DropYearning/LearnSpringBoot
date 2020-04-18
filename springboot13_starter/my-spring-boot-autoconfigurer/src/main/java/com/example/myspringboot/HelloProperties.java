package com.example.myspringboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 定义配置属性的类
 */

@ConfigurationProperties(prefix = "my.hello")
public class HelloProperties {
    private String prefix; //打招呼前缀
    private String suffix; //打招呼后缀、

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


}
