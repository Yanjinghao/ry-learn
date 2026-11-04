package com.ry.shop.controller;

import com.ry.shop.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "test")
@RestController
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Boolean>> health() {
        return Result.ok(Map.of("ok", true));
    }
}
