package com.ry.shop.controller;

import com.ry.shop.common.Result;
import com.ry.shop.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        Map<String, Object> data = userService.login(
            body.get("username"),
            body.get("password")
        );
        return Result.ok(data);
    }
}