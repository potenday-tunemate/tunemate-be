package com.tunemate.be.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.domain.user.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("user/")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("loginProcess")
    public String loginProcess(HttpSession session,CreateUserDTO userDto) {

        CreateUserDTO loginUser = userService.loginUserExist(userDto);

        if(loginUser == null) {
            return "user/loginFail";

        }else {
        
            session.setAttribute("loginUser", loginUser);
            return "redirect:/";
        }
    }
}
