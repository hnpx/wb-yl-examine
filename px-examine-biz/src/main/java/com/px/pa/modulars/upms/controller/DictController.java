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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.px.pa.modulars.upms.entity.SysDict;
import com.px.pa.modulars.upms.entity.SysDictItem;
import com.px.pa.modulars.upms.service.SysDictItemService;
import com.px.pa.modulars.upms.service.SysDictService;
import com.pig4cloud.pig.common.core.constant.CacheConstants;
import com.pig4cloud.pig.common.core.util.R;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2019-03-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dict")
@Api(value = "dict", tags = "字典管理模块")
public class DictController {

	private final SysDictItemService sysDictItemService;

	private final SysDictService sysDictService;

	/**
	 * 通过ID查询字典信息
	 * @param id ID
	 * @return 字典信息
	 */
	@GetMapping("/{id}")
	public R getById(@PathVariable Integer id) {
		return R.ok(sysDictService.getById(id));
	}

	/**
	 * 分页查询字典信息
	 * @param page 分页对象
	 * @return 分页对象
	 */
	@GetMapping("/page")
	public R<IPage> getDictPage(Page page, SysDict sysDict) {
		return R.ok(sysDictService.page(page, Wrappers.query(sysDict)));
	}

	/**
	 * 通过字典类型查找字典
	 * @param type 类型
	 * @return 同类型字典
	 */
	@GetMapping("/type/{type}")
	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type")
	public R getDictByType(@PathVariable String type) {
		return R.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getType, type)));
	}

	/**
	 * 添加字典
	 * @param sysDict 字典信息
	 * @return success、false
	 */
//	@SysLog("添加字典")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
	public R save(@Valid @RequestBody SysDict sysDict) {
		return R.ok(sysDictService.save(sysDict));
	}

	/**
	 * 删除字典，并且清除字典缓存
	 * @param id ID
	 * @return R
	 *
	 */
//	@SysLog("删除字典")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_dict_del')")
	public R removeById(@PathVariable Integer id) {
		sysDictService.removeDict(id);
		return R.ok();
	}

	/**
	 * 修改字典
	 * @param sysDict 字典信息
	 * @return success/false
	 */
	@PutMapping
//	@SysLog("修改字典")
	@PreAuthorize("@pms.hasPermission('sys_dict_edit')")
	public R updateById(@Valid @RequestBody SysDict sysDict) {
		sysDictService.updateDict(sysDict);
		return R.ok();
	}

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysDictItem 字典项
	 * @return
	 */
	@GetMapping("/item/page")
	public R getSysDictItemPage(Page page, SysDictItem sysDictItem) {
		return R.ok(sysDictItemService.page(page, Wrappers.query(sysDictItem)));
	}

	/**
	 * 通过id查询字典项
	 * @param id id
	 * @return R
	 */
	@GetMapping("/item/{id}")
	public R getDictItemById(@PathVariable("id") Integer id) {
		return R.ok(sysDictItemService.getById(id));
	}

	/**
	 * 新增字典项
	 * @param sysDictItem 字典项
	 * @return R
	 */
//	@SysLog("新增字典项")
	@PostMapping("/item")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R save(@RequestBody SysDictItem sysDictItem) {
		return R.ok(sysDictItemService.save(sysDictItem));
	}

	/**
	 * 修改字典项
	 * @param sysDictItem 字典项
	 * @return R
	 */
//	@SysLog("修改字典项")
	@PutMapping("/item")
	public R updateById(@RequestBody SysDictItem sysDictItem) {
		return sysDictItemService.updateDictItem(sysDictItem);
	}

	/**
	 * 通过id删除字典项
	 * @param id id
	 * @return R
	 */
//	@SysLog("删除字典项")
	@DeleteMapping("/item/{id}")
	public R removeDictItemById(@PathVariable Integer id) {
		return sysDictItemService.removeDictItem(id);
	}

}
