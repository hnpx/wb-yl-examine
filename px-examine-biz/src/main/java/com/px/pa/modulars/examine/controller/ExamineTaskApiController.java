package com.px.pa.modulars.examine.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.base.BaseApiController;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.constants.TypeEnum;
import com.px.pa.modulars.auth.service.RemoteTokenServiceImpl;
import com.px.pa.modulars.base.entity.BaseTasktypeItem;
import com.px.pa.modulars.base.entity.DataAnalysis;
import com.px.pa.modulars.base.service.BaseTasktypeItemService;
import com.px.pa.modulars.base.service.DataAnalysisService;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.service.ExamineImgService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.*;
import com.px.pa.modulars.examine.wrapper.ExamineTaskWrapper;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ????????????????????????
 */
@RestController
@RequestMapping("/api/examine/task/handler")
@Api("????????????????????????")
@Inner(false)
public class ExamineTaskApiController extends BaseApiController {
    @Autowired
    private ExamineTaskService taskService;
    @Autowired
    private BaseTasktypeService tasktypeService;
    @Autowired
    private RemoteTokenServiceImpl tokenService;
    @Autowired
    private  ExamineTaskService examineTaskService;
    @Autowired
    private ShopUserService shopUserService;
    @Autowired
    private ExamineTaskWrapper examineTaskWrapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private DataAnalysisService dataAnalysisService;
    @Resource
    private BaseTasktypeService baseTasktypeService;
    @Autowired
    private ExamineTaskItemService examineTaskItemService;
    @Autowired
    private ExamineImgService examineImgService;
    @Autowired
    private BaseTasktypeItemService baseTasktypeItemService;



    @ApiOperation("???????????????????????????????????????????????? ??????????????????")
    @GetMapping("/read/{taskId}")
    public Object readRoom(@PathVariable("taskId") Integer taskId) {
        TaskRoomResult result = this.taskService.readRoom(taskId);

        return R.ok(result);
    }

    @ApiOperation("???????????????????????????????????????????????? ??????????????????")
    @GetMapping("/read/type/{typeId}")
    public Object readTypeDetail(@PathVariable("typeId") Integer typeId) {
        BaseTasktype type = this.tasktypeService.getById(typeId);
        return R.ok(type);
    }

    /**
     * ????????????
     * 1?????????????????????????????????????????????????????????????????????
     * 2?????????????????????????????????????????????
     * 3????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * 4???????????????????????????????????????????????????????????????????????????
     *
     * @return ???????????????????????????
     */
    @PostMapping("/into")
    @ApiOperation(value = "??????????????????", notes = "??????true??????????????????????????????????????????")
    public Object intoRoom(@RequestBody IntoTaskRoomParam param) {
        //??????????????????????????????????????????????????????????????????

        boolean flag = this.taskService.intoRoom(super.getCurrentId(), param);
        return R.ok(flag);
    }



    /**
     * ????????????
     * @param examineTask ????????????
     * @return R
     */
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    // @SysLog("??????????????????" )
    @PostMapping("/apply")
    public R save1(@RequestBody ExamineTask examineTask) {

        Integer id = super.getCurrentId();
        ShopUser shopUser = this.shopUserService.getById(id);
//        shopUser.setPhone(examineTask.getShopTel());
        examineTask.setShopTel(shopUser.getPhone());
        examineTask.setUserName(examineTask.getOtherUser());
        //???????????????????????????
        if (StrUtil.isEmpty(shopUser.getName())) {
            shopUser.setName(examineTask.getUserName());
            shopUser.setIdCard(examineTask.getIdCard());
            this.shopUserService.updateById(shopUser);
        } else {
            examineTask.setUserName(shopUser.getName());
            examineTask.setIdCard(shopUser.getIdCard());
        }


//?????????????????????????????????
        if (this.checkTask(examineTask.getUserName(), examineTask.getShopName(), examineTask.getIdCard(),  examineTask.getTasktype())) {
            return R.failed("???????????????????????????????????????????????????????????????");
        }

        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //?????????????????????????????????
        DataAnalysis dataAnalysis = new DataAnalysis();
        dataAnalysis.setTime(sdf.format(date));
        Wrapper<DataAnalysis> qw1 = new QueryWrapper<>(dataAnalysis);
        DataAnalysis dataAnalysis1 = dataAnalysisService.getOne(qw1);
        DataAnalysis dataAnalysis2 = new DataAnalysis();
        if(dataAnalysis1 == null){
            dataAnalysis2.setTime(sdf.format(date));
            dataAnalysis2.setApplynum(1);
            dataAnalysisService.save(dataAnalysis2);
        }else {
            dataAnalysis2.setApplynum(dataAnalysis1.getApplynum()+1);
            dataAnalysisService.update(dataAnalysis2,qw1);
        }
        //????????????
       examineTask.setCreateTime(LocalDateTime.now());
        examineTask.setCreateBy(id);
       examineTask.setUpdateTime(LocalDateTime.now());
       examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue());
        examineTask.setType(TypeEnum.TYPE_ENUM_TWO.getValue());

        return R.ok(examineTaskService.save(examineTask));
    }



    /**
     * ????????????????????????????????????????????????
     *
     * @param name
     * @param shopName
     * @param code
     * @return
     */
    private boolean checkTask(String name, String shopName, String code, Integer taskType) {
        ExamineTask param = new ExamineTask();
        param.setUserName(name);
        param.setShopName(shopName);
//        param.setShopTel(phone);
//        param.setIdCard(code);
//        param.setTasktype(taskType);
        try {
            ExamineTask task = this.examineTaskService.getOne(new QueryWrapper<>(param));
            if (task != null) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }



    /**
     * ????????????
     * @return R
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @GetMapping("/record")
    @Inner(value = false)
    public R record(@RequestParam(required = false) Integer page,@RequestParam(required = false) Integer pageSize,
                    @RequestParam(required = false) Integer tasktype,@RequestParam("shop") Integer shop,
                    @RequestParam(required = false) Integer status) {
                   Page page1 = new Page(page,pageSize);
                   Page<Map<String,Object>> info = examineTaskService.getPageList(page1,tasktype,shop,status);
                   Page<ExamineTaskVo> pv = examineTaskWrapper.getVoPage(info);
                     return R.ok(pv);
    }



    /**
     * ??????????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    // @SysLog("??????????????????" )
    @GetMapping("/record/details")
    @Inner(value = false)
    public R record(@RequestParam("id") Integer id) {
        ExamineTaskVo examineTaskVo = new ExamineTaskVo();
        ExamineTask examineTask = examineTaskService.getById(id);

        BeanUtil.copyProperties(examineTask,examineTaskVo);
        if(examineTask.getEvaluation() == null){
            examineTaskVo.setEvaluation(0);
        }
        try{
         SysUser sysUser = sysUserService.getById(examineTaskVo.getContacts());
            examineTaskVo.setPhone(sysUser.getPhone());
        }catch (Exception e){
            examineTaskVo.setPhone("");
        }
       SysUser sysUser = sysUserService.getById(examineTask.getContacts());
        examineTaskVo.setContactsName(sysUser.getName());
         BaseTasktype baseTasktype =  baseTasktypeService.getById(examineTask.getTasktype());
        examineTaskVo.setTasktypeName(baseTasktype.getName());

        List<ExamineItemRVo> examineItemRVoList = new ArrayList<>();
        List<ExamineTaskItem> examineTaskItemList =  examineTaskItemService.list(new QueryWrapper<ExamineTaskItem>()
                .eq("tasktype",examineTask.getTasktype()).eq("shop",examineTask.getUser()).eq("examine_task",examineTask.getId()).groupBy("totle"));

        List<BaseTasktypeItem> baseTasktypeItemList =   baseTasktypeItemService.list(new QueryWrapper<BaseTasktypeItem>().eq("tasktype",examineTask.getTasktype()).groupBy("totle").orderByAsc("id"));
        baseTasktypeItemList.forEach(a-> {
            ExamineItemRVo examineItemRVo = new ExamineItemRVo();
            examineItemRVo.setName(a.getTotle());
            BaseTasktypeItem baseTasktypeItem = new BaseTasktypeItem();
            baseTasktypeItem.setTotle(a.getTotle());
            baseTasktypeItem.setTasktype(a.getTasktype());
            Wrapper<BaseTasktypeItem> qw = new QueryWrapper<>(baseTasktypeItem);
            List<BaseTasktypeItem> baseTasktypeItemList1 = baseTasktypeItemService.list(qw);
            List<ExamineTaskItem> examineTaskItemList1 = new ArrayList<>();
            baseTasktypeItemList1.forEach(b -> {
                ExamineTaskItem examineTaskItem = new ExamineTaskItem();
                examineTaskItem.setInfo(b.getInfo());
                examineTaskItem.setTotle(b.getTotle());
                examineTaskItem.setTasktype(b.getTasktype());
                examineTaskItem.setExamineTask(examineTaskVo.getId());
                examineTaskItem.setTasktypeItem(b.getId());
                examineTaskItem.setShop(examineTask.getUser());

                ExamineTaskItem examineTaskItem1 = new ExamineTaskItem();
                examineTaskItem1.setExamineTask(examineTaskVo.getId());
                examineTaskItem1.setShop(examineTask.getUser());
                examineTaskItem1.setTasktypeItem(b.getId());
                Wrapper<ExamineTaskItem> qw1 = new QueryWrapper<>(examineTaskItem1);
                ExamineTaskItem examineTaskItem2 = examineTaskItemService.getOne(qw1);
                if (examineTaskItem2 != null) {
                    examineTaskItem.setStatus(examineTaskItem2.getStatus());
                }
                ExamineImg examineImg = new ExamineImg();
                examineImg.setShop(examineTask.getUser());
                examineImg.setExamineTask(examineTaskVo.getId());
                examineImg.setExamineTaskItem(b.getId());
                Wrapper<ExamineImg> qw2 = new QueryWrapper<>(examineImg);
                ExamineImg examineImg1 =  examineImgService.getOne(qw2);
                if(examineImg1 != null){
                    examineTaskItem.setImg(examineImg1.getImgUrl());
                }
                examineTaskItemList1.add(examineTaskItem);
                examineItemRVo.setExamineTaskItemList(examineTaskItemList1);

            });

            examineItemRVoList.add(examineItemRVo);

        });

      /*  examineTaskItemList.forEach(a->{
            ExamineItemRVo examineItemRVo = new ExamineItemRVo();
            examineItemRVo.setName(a.getTotle());
            ExamineTaskItem examineTaskItem = new ExamineTaskItem();
            examineTaskItem.setShop(a.getShop());
            examineTaskItem.setTotle(a.getTotle());
            examineTaskItem.setTasktype(a.getTasktype());
            examineTaskItem.setTasktypeItem(a.getTasktypeItem());
            Wrapper<ExamineTaskItem> qw = new QueryWrapper<>(examineTaskItem);
            List<ExamineTaskItem> examineTaskItemList1 = examineTaskItemService.list(qw);
            examineTaskItemList1.forEach(b->{
                ExamineImg examineImg = new ExamineImg();
                examineImg.setShop(b.getShop());
                examineImg.setExamineTaskItem(b.getTasktypeItem());
                examineImg.setExamineTask(b.getExamineTask());
                Wrapper<ExamineImg> qw1 = new QueryWrapper<>(examineImg);
                ExamineImg examineImg1 = examineImgService.getOne(qw1);
                b.setImg(examineImg1.getImgUrl());
                BaseTasktypeItem baseTasktypeItem =  baseTasktypeItemService.getById(b.getTasktypeItem());
                b.setInfo(baseTasktypeItem.getInfo());

            });

            examineItemRVo.setExamineTaskItemList(examineTaskItemList1);
            examineItemRVoList.add(examineItemRVo);
        });*/


        Map<String,Object> map = new HashMap<>();
        map.put("examineTaskItemList",examineItemRVoList);
        map.put("examineTaskVo",examineTaskVo);

        return R.ok(map);
    }



    /**
     * ??????
     * @return R
     */
    @ApiOperation(value = "??????", notes = "??????")
    @GetMapping("/evaluation")
    @Inner(value = false)
    public Object getevaluation(@RequestParam("id") Integer id, @RequestParam("evaluation") Integer evaluation){

        ExamineTask examineTask = new ExamineTask();
        examineTask.setId(id);
        examineTask.setEvaluation(evaluation);
        examineTaskService.updateById(examineTask);
        return R.ok();

    }



}
