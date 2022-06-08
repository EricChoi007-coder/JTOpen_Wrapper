package com.scl.as400wrapper.controller;

import com.scl.as400wrapper.domain.User;
import com.scl.as400wrapper.service.userService.IUserService;
import com.scl.as400wrapper.utilis.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("jwt")
public class JWTController {

     @Autowired
    private IUserService userService;

    @GetMapping("/generate")
    public JsonData login(User user) {
        Map<String, Object> map = userService.login(user);
             //put token to response header
        if(map != null){
            //response.setHeader(JWTUtils.USER_LOGIN_TOKEN, (String) map.get("token"));
            return JsonData.buildSuccess(map);
        }else{
            return JsonData.buildError("Cannot Generate Token");
        }
    }

}
