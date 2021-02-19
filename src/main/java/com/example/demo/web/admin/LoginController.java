package com.example.demo.web.admin;


import com.example.demo.po.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.MD5util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String LoginPage(){
        return "admin/login";
    }

    @PostMapping("/login")  //post方法
    public String Login(@RequestParam String username,
                        @RequestParam String password ,
                        HttpSession session,
                        RedirectAttributes attributes){// 查询用户名密码并放到session中
        User user = userService.checkUser(username,MD5util.code(password));
        if(user != null){
            user.setPassword(null);
            session.setAttribute("user",user);
            return "admin/index";
        }else{
            attributes.addFlashAttribute("message","用户名或者密码错误");
            return "redirect:/admin"; //不能直接写return "admin/login",redirect 重定向
        }
    }

    @GetMapping("/logout")
    public String Logout(HttpSession session){  //注销登录
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}
