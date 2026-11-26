package com.ry.shop.dto.auth;

import java.util.List;

public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String nickname;

    // 登录新增角色, 权限, 菜单
    private List<String> roles;        // ["admin"] 或 ["shop"]
    private List<String> permissions;  // ["product:list", "order:pay", ...]
    private List<MenuVO> menus;        // 树：目录 + 菜单（不要按钮）

    public LoginResponse() {
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<MenuVO> getMenus() { return menus; }
    public void setMenus(List<MenuVO> menus) { this.menus = menus; }
}
