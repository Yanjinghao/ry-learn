package com.ry.shop.controller;

import com.ry.shop.common.Result;
import com.ry.shop.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.ry.shop.dto.LoginRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // @PostMapping("/login")
    // public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
    //     Map<String, Object> data = userService.login(
    //         body.get("username"),
    //         body.get("password")
    //     );
    //     return Result.ok(data);
    // }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        Map<String, Object> data = userService.login(
            req.getUsername(),
            req.getPassword()
        );
        return Result.ok(data);
    }
}