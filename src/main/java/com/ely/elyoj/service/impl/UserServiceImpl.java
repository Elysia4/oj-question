package com.ely.elyoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ely.elyoj.common.ErrorCode;
import com.ely.elyoj.config.JwtUtil;
import com.ely.elyoj.exception.BusinessException;
import com.ely.elyoj.mapper.UserMapper;
import com.ely.elyoj.model.dto.user.UserLoginRequest;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.LoginUserVO;
import com.ely.elyoj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest) {
        // 参数校验
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();
        
        System.out.println("登录请求 - 用户名: " + username);
        System.out.println("登录请求 - 密码: " + password);
        
        // 先查询用户，打印出数据库中的密码哈希值
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User dbUser = this.getOne(queryWrapper);
        if (dbUser != null) {
            System.out.println("数据库中用户密码哈希: " + dbUser.getPassword());
        }
        
        // 认证
        UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            System.out.println("认证失败 - 异常信息: " + e.getMessage());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        
        // 获取认证用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // 根据用户名查询用户信息
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        
        // 生成token
        String token = jwtUtil.generateToken(userDetails);
        
        // 封装并返回用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setToken(token);
        
        return loginUserVO;
    }

    @Override
    public LoginUserVO getLoginUser(String token) {
        if (token == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        
        // 从token中解析用户名
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "登录状态已过期");
        }
        
        // 根据用户名查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户不存在");
        }
        
        // 封装并返回用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setToken(token);
        
        return loginUserVO;
    }

    @Override
    public void userLogout(String token) {
        // 目前使用的是JWT，服务端不需要存储token，所以这里实际上不需要做什么
        // 如果以后有需要(比如黑名单)，可以在这里实现
        SecurityContextHolder.clearContext();
    }
} 