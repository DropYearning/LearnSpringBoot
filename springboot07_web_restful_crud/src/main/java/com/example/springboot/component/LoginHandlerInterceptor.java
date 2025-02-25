package com.example.springboot.component;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于检查登陆状态的拦截器，实现登陆检查
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {

    // 登陆前检查
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if (user == null){
            // 未登陆，返回登陆页面
            request.setAttribute("msg", "没有权限，请先登陆");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        }else{
            // 已登陆，放行
            return true;
        }
    }

}
