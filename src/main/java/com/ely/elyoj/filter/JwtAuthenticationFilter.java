package com.ely.elyoj.filter;

import com.ely.elyoj.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Value("${ely.security.jwt.header}")
    private String headerName;

    @Value("${ely.security.jwt.token-prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(headerName);
        final String jwt;
        final String username;

        // 如果没有认证头或认证头不是以指定前缀开头，直接放行
        if (authHeader == null || !authHeader.startsWith(tokenPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT - 修复：确保正确截取token部分，去掉前缀和空格
        jwt = authHeader.substring(tokenPrefix.length()).trim();

        try {
            // 从JWT中提取用户名
            username = jwtUtil.extractUsername(jwt);

            // 如果用户名不为空且当前没有认证
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 从用户详情服务中获取用户详情
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 验证token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // 设置认证令牌的详情
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 将认证令牌设置到安全上下文中
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        // 放行
        filterChain.doFilter(request, response);
    }
} 