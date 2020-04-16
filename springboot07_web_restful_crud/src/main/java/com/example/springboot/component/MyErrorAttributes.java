package com.example.springboot.component;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

// 在容器中加入我们自定义的错误属性类
@Component
public class MyErrorAttributes extends DefaultErrorAttributes {
    public MyErrorAttributes() {
        super(true);
    }

    // 返回的errorAttributes就是页面和JSON能获取到所有字段
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {

        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest,includeStackTrace );
        errorAttributes.put("company", "mycompany");
        // 我们的异常处理器携带的数据
        Map<String, Object> ext = (Map<String, Object>) webRequest.getAttribute("ext", 0);
        errorAttributes.put("ext", ext);
        return errorAttributes;
    }
}
