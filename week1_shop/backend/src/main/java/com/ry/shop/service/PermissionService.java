package com.ry.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ry.shop.dto.auth.MenuVO;
import com.ry.shop.entity.Menu;
import com.ry.shop.entity.Role;
import com.ry.shop.entity.RoleMenu;
import com.ry.shop.entity.UserRole;
import com.ry.shop.mapper.MenuMapper;
import com.ry.shop.mapper.RoleMapper;
import com.ry.shop.mapper.RoleMenuMapper;
import com.ry.shop.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PermissionService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    public PermissionService(UserRoleMapper userRoleMapper,
                             RoleMapper roleMapper,
                             RoleMenuMapper roleMenuMapper,
                             MenuMapper menuMapper) {
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.menuMapper = menuMapper;
    }

    /** 查用户有哪些角色 code，例如 ["admin"] 或 ["shop"] */
    public List<String> listRoleCodes(Long userId) {
        // 1. 在 user_role 里查出这个用户绑定的角色 id
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 把 roleId 收集到列表里
        List<Long> roleIds = new ArrayList<>();
        for (UserRole ur : userRoles) {
            roleIds.add(ur.getRoleId());
        }

        // 3. 按 roleId 批量查出 role 表
        List<Role> roles = roleMapper.selectBatchIds(roleIds);

        // 4. 取出 code，装进结果
        List<String> codes = new ArrayList<>();
        for (Role role : roles) {
            if (role.getCode() != null) {
                codes.add(role.getCode());
            }
        }
        return codes;
    }

    /** 查用户能看到的全部菜单（含按钮），按 sort 排序 */
    public List<Menu> listMenusByUserId(Long userId) {
        // 1. 用户 → 角色
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = new ArrayList<>();
        for (UserRole ur : userRoles) {
            roleIds.add(ur.getRoleId());
        }

        // 2. 角色 → 菜单 id（用 Set 去重：多个角色可能有同一菜单）
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId, roleIds));
        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> menuIds = new HashSet<>();
        for (RoleMenu rm : roleMenus) {
            menuIds.add(rm.getMenuId());
        }

        // 3. 按菜单 id 查出 menu，再按 sort 排一下
        List<Menu> menus = menuMapper.selectBatchIds(menuIds);
        menus.sort((a, b) -> {
            int sa = a.getSort() == null ? 0 : a.getSort();
            int sb = b.getSort() == null ? 0 : b.getSort();
            return Integer.compare(sa, sb);
        });
        return menus;
    }

    public List<String> listPermissions(Long userId) {
        return listMenusByUserId(userId).stream()
                // 取出perms列
                .map(Menu::getPerms)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .toList();
    }

    public List<MenuVO> buildMenuTree(Long userId) {
        List<Menu> all = listMenusByUserId(userId).stream()
                .filter(m -> m.getType() != null && m.getType() != Menu.TYPE_BUTTON)
                .filter(m -> m.getVisible() == null || m.getVisible() == 1)
                .toList();

        Map<Long, MenuVO> map = new LinkedHashMap<>();
        for (Menu m : all) {
            map.put(m.getId(), toVo(m));
        }

        List<MenuVO> roots = new ArrayList<>();
        for (MenuVO vo : map.values()) {
            Long pid = vo.getParentId() == null ? 0L : vo.getParentId();
            if (pid == 0L) {
                roots.add(vo);
            } else {
                MenuVO parent = map.get(pid);
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        return roots;
    }

    private MenuVO toVo(Menu m) {
        MenuVO vo = new MenuVO();
        vo.setId(m.getId());
        vo.setParentId(m.getParentId());
        vo.setName(m.getName());
        vo.setType(m.getType());
        vo.setPath(m.getPath());
        vo.setComponent(m.getComponent());
        vo.setPerms(m.getPerms());
        vo.setSort(m.getSort());
        return vo;
    }
}