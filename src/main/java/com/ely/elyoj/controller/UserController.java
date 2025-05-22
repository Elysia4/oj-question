package com.ely.elyoj.controller;

import com.ely.elyoj.common.BaseResponse;
import com.ely.elyoj.common.ResultUtils;
import com.ely.elyoj.model.dto.user.UserLoginRequest;
import com.ely.elyoj.model.vo.LoginUserVO;
import com.ely.elyoj.service.UserService;
import com.ely.elyoj.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserHolder userHolder;

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求
     * @return 登录用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户信息
     * @param request HTTP请求
     * @return 登录用户信息
     */
    @GetMapping("/current")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        LoginUserVO loginUserVO = userService.getLoginUser(token);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户登出
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.userLogout(token);
        return ResultUtils.success(true);
    }
} 