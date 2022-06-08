package com.scl.as400wrapper.service.userService;

import com.scl.as400wrapper.domain.User;
import com.scl.as400wrapper.utilis.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;

@Configuration
@PropertySource("classpath:applist.properties")
@Service
public class UserService implements IUserService {

    @Value("${app_list}")
    private String appList;

    @Override
    public Map<String,Object> login (User user) {
        //check name field
        if (user.getName() == null || user.getName().equals("")) {
            return null;
        }
        //validate user in config file
        String username = user.getName();
        List<String> list = new ArrayList<String>(Arrays.asList(appList.split(",")));
        if(list.contains(username)){
            //将userId存入token中
            String token = JWTUtils.createToken(user.getName());
            Map<String,Object> map = new HashMap<>();
            map.put("user",user.getName());
            map.put("token",token);
            return map;
        }else {
            return null;
        }
    }
}
