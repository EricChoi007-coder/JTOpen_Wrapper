package com.scl.as400wrapper.service.userService;

import com.scl.as400wrapper.domain.User;

import java.util.Map;

public interface IUserService {
    Map<String,Object> login(User user);
}
