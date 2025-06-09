package com.ely.elyoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ely.elyoj.common.ErrorCode;
import com.ely.elyoj.config.JwtUtil;
import com.ely.elyoj.exception.BusinessException;
import com.ely.elyoj.mapper.UserMapper;
import com.ely.elyoj.model.dto.user.UserLoginRequest;
import com.ely.elyoj.model.dto.user.UserRegisterRequest;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.LoginUserVO;
import com.ely.elyoj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 参数校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数为空");
        }
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        
        // 校验参数是否为空
        if (StringUtils.isAnyBlank(username, password, checkPassword, email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 校验密码长度（6-20位）
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在6-20之间");
        }
        
        // 校验用户名长度（3-20位）
        if (username.length() < 3 || username.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在3-20之间");
        }
        
        // 校验两次密码是否一致
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        
        // 校验用户名是否重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }
        
        // 校验邮箱是否重复
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        // 对密码进行加密
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setEmail(email);
        // 设置默认角色为user
        user.setRole("user");
        // 设置默认状态为正常
        user.setStatus(0);
        // 设置创建和更新时间
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 保存用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        
        return user.getId();
    }

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
    
    @Override
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        // 检查用户名长度
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        long count = this.count(queryWrapper);
        
        return count == 0; // 如果count为0，表示用户名可用
    }
    
    @Override
    public boolean isEmailAvailable(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // 简单的邮箱格式验证
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            return false;
        }
        
        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        long count = this.count(queryWrapper);
        
        return count == 0; // 如果count为0，表示邮箱可用
    }
} 