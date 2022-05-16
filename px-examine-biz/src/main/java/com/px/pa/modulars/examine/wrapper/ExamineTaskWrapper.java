package com.px.pa.modulars.examine.wrapper;

import cn.hutool.core.bean.BeanUtil;

import com.px.pa.core.wrapper.BaseWrapper;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.examine.vo.ExamineTaskVo;

import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ExamineTaskWrapper extends BaseWrapper<Map<String,Object>, ExamineTaskVo> {

  @Resource
  private BaseTasktypeService baseTasktypeService;
  @Resource
  private SysUserService sysUserService;
    @Override
    public ExamineTaskVo wrap(Map<String, Object> item) {
        ExamineTaskVo vo = new ExamineTaskVo();
        BeanUtil.copyProperties(item, vo);
        try{
            BaseTasktype baseTasktype = baseTasktypeService.getById(Integer.parseInt(item.get("tasktype").toString()));
            vo.setTasktypeName(baseTasktype.getName());
        }catch (Exception e){
            vo.setTasktypeName("");
        }

        try{
        SysUser sysUser = sysUserService.getById(Integer.parseInt(item.get("contacts").toString()));
        vo.setContactsName(sysUser.getUsername());
        }catch (Exception e){
            vo.setContactsName("");
        }

        return vo;
    }
}
