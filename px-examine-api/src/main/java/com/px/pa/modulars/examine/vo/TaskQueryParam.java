package com.px.pa.modulars.examine.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.pig4cloud.pig.common.core.core.vo.BasePageParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zhouz
 */
@Data
@ToString
@ApiModel("踏勘任务查询")
public class TaskQueryParam extends BasePageParamVo {
    /**
     * 申请人
     */
    @ApiModelProperty(value = "申请人")
    private Integer user;
    @ApiModelProperty(value = "店铺名称")
    private String shopName;
    @ApiModelProperty(value = "店铺联系电话")
    private String shopTel;
    @ApiModelProperty(value = "统一社会信用代码")
    private String shopNum;
    @ApiModelProperty(value = "审核员")
    private Integer contacts;
    @ApiModelProperty(value = "视频房间号")
    private String videoNum;
    @ApiModelProperty(value = "创建人")
    private Integer createBy;
    @ApiModelProperty(value = "删除标记")
    private Integer enable;
    @ApiModelProperty(value = "任务分类")
    private Integer tasktype;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "预约开始时间")
    private Date applyStartTime;
    @ApiModelProperty(value = "预约结束时间")
    private Date applyEndTime;

}
