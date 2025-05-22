package com.ely.elyoj;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

/**
 * 密码编码器测试类
 * 用于生成不同加密方式的密码
 */
public class PasswordEncoderTest {

    public static void main(String[] args) {
        // 数据库中存储的密码
        String dbPassword = "0192023a7bbd73250516f069df18b500";
        
        // 测试常见的密码组合
        testPassword("admin", dbPassword);
        testPassword("admin123", dbPassword);
        testPassword("123456", dbPassword);
        testPassword("password", dbPassword);
        testPassword("Admin123", dbPassword);
        testPassword("a123456", dbPassword);
        testPassword("admin@123", dbPassword);
    }
    
    private static void testPassword(String rawPassword, String dbPassword) {
        System.out.println("\n测试密码: " + rawPassword);
        System.out.println("==========================");
        
        // 使用MD5加密
        String md5Password = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        
        // 输出结果
        System.out.println("MD5加密后: " + md5Password);
        boolean isMatch = md5Password.equals(dbPassword);
        System.out.println("与数据库密码匹配: " + isMatch);
        
        if (isMatch) {
            System.out.println("找到匹配的密码: " + rawPassword);
            System.out.println("==========================");
        }
    }
} 