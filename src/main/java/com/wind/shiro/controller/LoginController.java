package com.wind.shiro.controller;

import com.wind.shiro.exception.NotBindException;
import com.wind.shiro.pojo.ActiveUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author LILANDONG
 */
@Controller
public class LoginController {
    @RequestMapping("/mainpage")
    public String mainpage(Model model) throws Exception {
        //从shiro的session中取activeUser
        Subject subject = SecurityUtils.getSubject();
        //取身份信息
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        //通过model传到页面
        model.addAttribute("activeUser", activeUser);
        return "shiro";
    }

    /**
     * 抛出未绑定异常时进行转发页面处理
     */
    @ExceptionHandler(value = NotBindException.class)
    public ModelAndView notBindHandler(NotBindException e) {
        return new ModelAndView("redirect:" + e.getRedirectUrl());
    }

    @RequestMapping("/detail")
    @ResponseBody
    public Object detail(HttpServletRequest request) {
        return request.getUserPrincipal();
    }
}

