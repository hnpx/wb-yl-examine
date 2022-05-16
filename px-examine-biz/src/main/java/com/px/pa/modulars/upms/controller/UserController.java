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

package com.px.pa.modulars.upms.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.px.pa.constants.Constants;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.px.pa.constants.Constants;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.UserPassVo;
import com.px.pa.modulars.upms.dto.UserDTO;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.px.pa.modulars.upms.vo.UserInfoListParam;
import com.px.pa.modulars.upms.vo.UserInfoListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lengleng
 * @date 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(value = "user", tags = "用户管理模块")
public class UserController {

    private final SysUserService userService;
	private final SysUserRoleService sysUserRoleService;

    private final RedisHelper redisHelper;
    private final ExamineTaskService examineTaskService;

    /**
     * 获取当前用户全部信息
     *
     * @return 用户信息
     */
    @GetMapping(value = {"/info"})
    public R info() {
        String username = SecurityUtils.getUser().getUsername();
        SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.failed("获取当前用户信息失败");
        }
        return R.ok(userService.getUserInfo(user));
    }

    /**
     * 获取指定用户全部信息
     *
     * @return 用户信息
     */
    @Inner
    @GetMapping("/info/{username}")
    public R info(@PathVariable String username) {
        SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.failed(String.format("用户信息为空 %s", username));
        }
        return R.ok(userService.getUserInfo(user));
    }

    /**
     * 通过ID查询用户信息
     *
     * @param id ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public R user(@PathVariable Integer id) {
        return R.ok(userService.getUserVoById(id));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    @GetMapping("/details/{username}")
    public R user(@PathVariable String username) {
        SysUser condition = new SysUser();
        condition.setUsername(username);
        return R.ok(userService.getOne(new QueryWrapper<>(condition)));
    }

    /**
     * 删除用户信息
     *
     * @param id ID
     * @return R
     */
//	@SysLog("删除用户信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_user_del')")
    public R userDel(@PathVariable Integer id) {
        SysUser sysUser = userService.getById(id);
        return R.ok(userService.removeUserById(sysUser));
    }

    /**
     * 添加用户
     *
     * @param userDto 用户信息
     * @return success/false
     */
//	@SysLog("添加用户")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_user_add')")
    public R user(@RequestBody UserDTO userDto) {

        if(StringUtils.isNotEmpty(userDto.getPhone())){
          int n = userService.count(new QueryWrapper<SysUser>().eq("phone",userDto.getPhone()));
          if(n>0){
              return R.failed("此手机号已经被添加,请换其他的手机号");
          }

        }
        return R.ok(userService.saveUser(userDto));
    }

    /**
     * 更新用户信息
     *
     * @param userDto 用户信息
     * @return R
     */
//	@SysLog("更新用户信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_user_edit')")
    public R updateUser(@Valid @RequestBody UserDTO userDto) {
        SysUser sysUser =  userService.getById(userDto.getUserId());
        if(StringUtils.isNotEmpty(userDto.getPhone()) & !sysUser.getPhone().equals(userDto.getPhone())){
            int n = userService.count(new QueryWrapper<SysUser>().eq("phone",userDto.getPhone()));
            if(n>0){
                return R.failed("此手机号已经被添加,请换其他的手机号");
            }

        }
        return R.ok(userService.updateUser(userDto));
    }

    /**
     * 分页查询用户
     *
     * @param page    参数集
     * @param userDTO 查询参数列表
     * @return 用户集合
     */
    @GetMapping("/page")
    public R getUserPage(Page page, UserDTO userDTO) {
        return R.ok(userService.getUserWithRolePage(page, userDTO));
    }

    /**
     * 修改个人信息
     *
     * @param userDto userDto
     * @return success/false
     */
//	@SysLog("修改个人信息")
    @PutMapping("/edit")
    public R updateUserInfo(@Valid @RequestBody UserDTO userDto) {
        Integer userId = SecurityUtils.getUser().getId();
        SysUser sysUser =  userService.getById(userId);
        if(StringUtils.isNotEmpty(userDto.getPhone()) & !sysUser.getPhone().equals(userDto.getPhone())){
            int n = userService.count(new QueryWrapper<SysUser>().eq("phone",userDto.getPhone()));
            if(n>0){
                return R.failed("此手机号已经被添加,请换其他的手机号");
            }

        }
        return userService.updateUserInfo(userDto);
    }

    /**
     * @param username 用户名称
     * @return 上级部门用户列表
     */
    @GetMapping("/ancestor/{username}")
    public R listAncestorUsers(@PathVariable String username) {
        return R.ok(userService.listAncestorUsersByUsername(username));
    }


	/**
	 * 查询所有的业务员
	 * @return
	 */
	@ApiOperation(value = "查询所有的业务员", notes = "查询所有的业务员")
	@GetMapping("/select/all" )
	@Inner(value = false)
	public R getAll() {
		/*SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setRoleId(Constants.ROLE_YW);
		Wrapper<SysUserRole> qw = new QueryWrapper<>(sysUserRole);
		List<SysUserRole> sysUserRoleList = sysUserRoleService.list(qw);
		sysUserRoleList.forEach(a->{

		SysUser sysUser = userService.getById(a.getUserId());
		a.setName(sysUser.getName());
		});*/
        List<SysUserRole> sysUserRoleList =  sysUserRoleService.getList();

		return R.ok(sysUserRoleList);
	}

    @ApiOperation(value = "在线", tags = "在线")
    @GetMapping("/read/online")
    public R online() {
        Integer userId = SecurityUtils.getUser().getId();
        Object obj = this.redisHelper.get(Constants.USER_ONLINE_INFO + userId.toString());
        this.redisHelper.set(Constants.USER_ONLINE_INFO + userId.toString(), ((obj == null) ? new Boolean(false) : new Boolean(obj.toString())), Constants.USER_ONLINE_INFO_TIMEOUT);
        return R.ok();
    }

    @PutMapping("/read/user/list")
    public R queryUserList(UserInfoListParam param) {
        List<UserInfoListResult> users = this.userService.queryUserList(param);
        return R.ok(users);
    }
    @GetMapping("/read/user/list")
    public R queryUserList() {
        UserInfoListParam param=new UserInfoListParam();
        List<UserInfoListResult> users = this.userService.queryUserList(param);
        return R.ok(users);
    }


    /**
     * 查询所有的业务员的通过率
     * @return
     */
    @ApiOperation(value = "查询所有的业务员的通过率", notes = "查询所有的业务员的通过率")
    @GetMapping("/select/all/passRate" )
    @Inner(value = false)
    public R passRate() {
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId(Constants.ROLE_YW);
        Wrapper<SysUserRole> qw = new QueryWrapper<>(sysUserRole);
        List<SysUserRole> sysUserRoleList = sysUserRoleService.list(qw);
        List<UserPassVo> userPassVoList = new ArrayList<>();
        sysUserRoleList.forEach(a->{
            //查询用户的姓名
            SysUser sysUser =  userService.getById(a.getUserId());
            if("0".equals(sysUser.getDelFlag()) || "0".equals(sysUser.getLockFlag())){
                UserPassVo userPassVo = new UserPassVo();
                ExamineTask examineTask = new ExamineTask();
                examineTask.setContacts(a.getUserId());
                examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue());
                int pass = examineTaskService.count(new QueryWrapper<>(examineTask));
                ExamineTask examineTask1 = new ExamineTask();
                examineTask1.setContacts(a.getUserId());
                examineTask1.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue());
                int noPass = examineTaskService.count(new QueryWrapper<>(examineTask1));
                userPassVo.setId(a.getUserId());
                userPassVo.setName(sysUser.getName());
                userPassVo.setCount(pass + noPass);
                userPassVo.setPass(pass);
                int sum = pass + noPass;
                if(sum == 0){
                    userPassVo.setBili(0);
                }else {
                  BigDecimal bigDecimal1 = new BigDecimal(pass);
                    BigDecimal bigDecimal2 = new BigDecimal(sum);
                    userPassVo.setBili( Double.valueOf(bigDecimal1.divide(bigDecimal2, 2, BigDecimal.ROUND_HALF_UP).toString())*100);
                }

                userPassVoList.add(userPassVo);
            }

        });

        return R.ok(userPassVoList);
    }

}
