package com.ely.elyoj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ely.elyoj.mapper.UserMapper;
import com.ely.elyoj.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("加载用户: " + username);
        
        // 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        // 如果用户不存在，抛出异常
        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        System.out.println("数据库中用户信息: " + user);
        System.out.println("数据库中密码哈希: " + user.getPassword());
        
        // 如果用户被禁用，抛出异常
        if (user.getStatus() != 0) {
            System.out.println("用户已被禁用: " + username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 创建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 添加角色权限，注意添加ROLE_前缀
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

        // 返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
} 