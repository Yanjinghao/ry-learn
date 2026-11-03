package com.ry.shop.controller;

import com.ry.shop.common.Result;
import com.ry.shop.entity.User;
import com.ry.shop.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.ry.shop.dto.RegisterRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @PostMapping("/register")
    // public Result<User> register(@RequestBody Map<String, String> body) {
    //     User user = userService.register(
    //         body.get("username"),
    //         body.get("password"),
    //         body.get("nickname")
    //     );
    //     return Result.ok(user);
    // }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(
            req.getUsername(),
            req.getPassword(),
            req.getNickname()
        );
        return Result.ok(user);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<User>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        return Result.ok(userService.list(username, status));
    }

    @PutMapping("/{id}/nickname")
    public Result<Void> updateNickname(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        userService.updateNickname(id, body.get("nickname"));
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                       @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.ok();
    }
}