package com.ely.elyoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ely.elyoj.model.dto.user.UserLoginRequest;
import com.ely.elyoj.model.dto.user.UserRegisterRequest;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.LoginUserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求
     * @return 用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求
     * @return 登录用户VO，包含token等信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest);

    /**
     * 获取当前登录用户
     * @param token 用户token
     * @return 当前登录用户信息
     */
    LoginUserVO getLoginUser(String token);

    /**
     * 用户登出
     * @param token 用户token
     */
    void userLogout(String token);
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    boolean isEmailAvailable(String email);
} 