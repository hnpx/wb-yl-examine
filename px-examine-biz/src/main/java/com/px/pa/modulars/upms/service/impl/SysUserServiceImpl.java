/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.px.pa.modulars.upms.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.px.pa.constants.Constants;
import com.px.pa.modulars.upms.dto.UserDTO;
import com.px.pa.modulars.upms.dto.UserInfo;
import com.px.pa.modulars.upms.entity.SysDept;
import com.px.pa.modulars.upms.entity.SysRole;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.vo.MenuVO;
import com.px.pa.modulars.upms.vo.UserInfoListParam;
import com.px.pa.modulars.upms.vo.UserInfoListResult;
import com.px.pa.modulars.upms.vo.UserVO;
import com.px.pa.modulars.upms.mapper.SysUserMapper;
import com.px.pa.modulars.upms.service.*;
import com.pig4cloud.pig.common.core.constant.CacheConstants;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lengleng
 * @date 2019/2/1
 */
@Slf4j
@Service("sysUserServiceImpl")
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    //	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    @Autowired
    private PasswordEncoder encoding;
    @Autowired
    private SysMenuService sysMenuService;

    private final SysRoleService sysRoleService;

    private final SysDeptService sysDeptService;

    private final SysUserRoleService sysUserRoleService;

    private final RedisHelper redisHelper;

    /**
     * 保存用户信息
     *
     * @param userDto DTO 对象
     * @return success/fail
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveUser(UserDTO userDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setDelFlag(CommonConstants.STATUS_NORMAL);
        sysUser.setPassword(encoding.encode(userDto.getPassword()));
        baseMapper.insert(sysUser);
        List<SysUserRole> userRoleList = userDto.getRole().stream().map(roleId -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(sysUser.getUserId());
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());
        return sysUserRoleService.saveBatch(userRoleList);
    }

    @Override
    public List<UserInfoListResult> queryUserList(UserInfoListParam param) {
        SysUserRole urParam = new SysUserRole();
        //TODO 业务员角色ID固定
        urParam.setRoleId(6);
        List<SysUserRole> urs = this.sysUserRoleService.list(new QueryWrapper<>(urParam));
        QueryWrapper qw = new QueryWrapper();
        Integer[] userId = new Integer[urs.size()];
        for (int i = 0; i < urs.size(); i++) {
            userId[i] = urs.get(i).getUserId();
        }
        qw.in("user_id", userId);
        List<SysUser> users = this.list(qw);
        List<UserInfoListResult> result = new ArrayList<>();
        users.forEach(user -> {
            UserInfoListResult item = new UserInfoListResult();
            item.setName(user.getName());
            item.setUsername(user.getUsername());
            item.setPhone(user.getPhone());
            item.setId(user.getUserId());
            Object onlineObj = this.redisHelper.get(Constants.USER_ONLINE_INFO + user.getUserId().toString());
            if (onlineObj != null) {
                item.setOnline(true);
                Boolean online = (Boolean) onlineObj;
                item.setTask(online);
            } else {
                item.setOnline(false);
                item.setTask(false);
            }
            result.add(item);
        });
        return result;
    }

    /**
     * 通过查用户的全部信息
     *
     * @param sysUser 用户
     * @return
     */
    @Override
    public UserInfo getUserInfo(SysUser sysUser) {
        UserInfo userInfo = new UserInfo();
        userInfo.setSysUser(sysUser);
        // 设置角色列表 （ID）
        List<Integer> roleIds = sysRoleService.findRolesByUserId(sysUser.getUserId()).stream().map(SysRole::getRoleId)
                .collect(Collectors.toList());
        userInfo.setRoles(ArrayUtil.toArray(roleIds, Integer.class));

        // 设置权限列表（menu.permission）
        Set<String> permissions = new HashSet<>();
        roleIds.forEach(roleId -> {
            List<MenuVO> menus = sysMenuService.findMenuByRoleId(roleId);
            List<String> permissionList = menus.stream()
                    .filter(menuVo -> StringUtils.isNotEmpty(menuVo.getPermission())).map(MenuVO::getPermission)
                    .collect(Collectors.toList());
            permissions.addAll(permissionList);
        });
        userInfo.setPermissions(ArrayUtil.toArray(permissions, String.class));
        return userInfo;
    }

    /**
     * 分页查询用户信息（含有角色信息）
     *
     * @param page    分页对象
     * @param userDTO 参数列表
     * @return
     */
    @Override
    public IPage getUserWithRolePage(Page page, UserDTO userDTO) {
        return baseMapper.getUserVosPage(page, userDTO);
    }

    /**
     * 通过ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserVoById(Integer id) {
        return baseMapper.getUserVoById(id);
    }

    /**
     * 删除用户
     *
     * @param sysUser 用户
     * @return Boolean
     */
    @Override
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#sysUser.username")
    public Boolean removeUserById(SysUser sysUser) {
        sysUserRoleService.removeRoleByUserId(sysUser.getUserId());
        this.removeById(sysUser.getUserId());
        return Boolean.TRUE;
    }

    @Override
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
    public R updateUserInfo(UserDTO userDto) {
        UserVO userVO = baseMapper.getUserVoByUsername(userDto.getUsername());
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userVO.getUserId());
        if (Integer.valueOf(1).equals(userDto.getEditIdea())) {
            if (!encoding.matches(userDto.getPassword(), userVO.getPassword())) {
                return R.failed("原密码错误，修改失败");
            }
            sysUser.setPassword(encoding.encode(userDto.getNewpassword1()));
        } else {
            sysUser.setPhone(userDto.getPhone());
            sysUser.setAvatar(userDto.getAvatar());
        }
        return R.ok(this.updateById(sysUser));
    }

    @Override
    @CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
    public Boolean updateUser(UserDTO userDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setUpdateTime(LocalDateTime.now());

        if (StrUtil.isNotBlank(userDto.getPassword())) {
            sysUser.setPassword(encoding.encode(userDto.getPassword()));
        }
        this.updateById(sysUser);

        sysUserRoleService
                .remove(Wrappers.<SysUserRole>update().lambda().eq(SysUserRole::getUserId, userDto.getUserId()));
        userDto.getRole().forEach(roleId -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(sysUser.getUserId());
            userRole.setRoleId(roleId);
            userRole.insert();
        });
        return Boolean.TRUE;
    }

    /**
     * 查询上级部门的用户信息
     *
     * @param username 用户名
     * @return R
     */
    @Override
    public List<SysUser> listAncestorUsersByUsername(String username) {
        SysUser sysUser = this.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));

        SysDept sysDept = sysDeptService.getById(sysUser.getDeptId());
        if (sysDept == null) {
            return null;
        }

        Integer parentId = sysDept.getParentId();
        return this.list(Wrappers.<SysUser>query().lambda().eq(SysUser::getDeptId, parentId));
    }

    @Override
    public UserInfo readInfo(String username) {
        SysUser param = new SysUser();
        param.setUsername(username);
        QueryWrapper query = new QueryWrapper(param);
        SysUser user = this.baseMapper.selectOne(query);
        UserInfo userInfo = this.getUserInfo(user);
        return userInfo;
    }


}
