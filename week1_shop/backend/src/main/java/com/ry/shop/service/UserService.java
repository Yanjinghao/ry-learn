package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.shop.entity.User;
import com.ry.shop.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    // 构造器注入（推荐）
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User register(String username, String password, String nickname) {
        // 1. 查用户名是否已存在
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (count != null && count > 0) {
            throw new RuntimeException("用户名已存在"); // 周五再做成统一异常
        }

        // 2. 插入（密码先明文，周三再 BCrypt）
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
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
            throw new RuntimeException("用户不存在");
        }
        user.setNickname(nickname);
        userMapper.updateById(user);
    }

    public void deleteById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(id);
    }
}