package com.ely.elyoj.utils;

import com.ely.elyoj.common.ErrorCode;
import com.ely.elyoj.exception.BusinessException;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 用户信息持有工具类
 */
@Component
@RequiredArgsConstructor
public class UserHolder {

    private final UserService userService;

    /**
     * 获取当前登录用户
     * @param request HTTP请求
     * @return 当前用户
     */
    public User getLoginUser(HttpServletRequest request) {
        // 从Spring Security上下文中获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 获取用户名
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        
        String username = ((UserDetails) principal).getUsername();
        
        // 根据用户名查询用户
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        
        return user;
    }
} 