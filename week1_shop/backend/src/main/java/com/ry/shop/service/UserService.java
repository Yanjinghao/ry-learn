package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.shop.entity.User;
import com.ry.shop.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ry.shop.utils.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import com.ry.shop.common.BusinessException;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 构造器注入（推荐）
    public UserService(UserMapper userMapper,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(String username, String password, String nickname) {
        // 1. 查用户名是否已存在
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (count != null && count > 0) {
            // throw new RuntimeException("用户名已存在"); // 周五再做成统一异常
            throw new BusinessException("用户名已存在");
        }

        // 2. 插入（密码先明文，周三再 BCrypt）
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setStatus(1);
        userMapper.insert(user);
        // return user;
        return userMapper.selectById(user.getId());  // 再查一次, 能得到时间
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public List<User> list(String username, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 有传 username 才按用户名模糊查；没传就不过滤
        if (username != null && !username.isBlank()) {
            // wrapper.like("username", username);
            wrapper.like(User::getUsername, username);
        }
        // 有传 status 才按状态精确查
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getId);
        return userMapper.selectList(wrapper);
    }

    public void updateNickname(Long id, String nickname) {
        User user = userMapper.selectById(id);
        if (user == null) {
            // throw new RuntimeException("用户不存在");
            throw new BusinessException("用户不存在");
        }
        user.setNickname(nickname);
        userMapper.updateById(user);
    }

    public void updateStatus(Long id, Integer status) {
        if(status  == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不合法");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            // throw new RuntimeException("用户不存在");
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public void deleteById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(id);
    }

    public Map<String, Object> login(String username, String password) {
        // 1. 按用户名查
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    
        // 2. 用户不存在 或 密码不对 → 同一句错误（防枚举账号）
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            // throw new RuntimeException("用户名或密码错误");
            throw new BusinessException("用户名或密码错误");
        }
    
        // 3. 禁用用户（可选）
        if (user.getStatus() != null && user.getStatus() == 0) {
            // throw new RuntimeException("用户已被禁用");
            throw new BusinessException("用户已被禁用");
        }
    
        // 4. 签发 JWT
        String token = jwtUtil.createToken(user.getId(), user.getUsername());
    
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return data;
    }
}