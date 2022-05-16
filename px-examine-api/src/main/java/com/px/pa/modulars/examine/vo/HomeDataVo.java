package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HomeDataVo {
    @ApiModelProperty("平台任务数")
   private int examineNum;
    @ApiModelProperty("待审核数")
    private int waitApply;
    @ApiModelProperty("已审核数")
    private int alreadyApply;
    @ApiModelProperty("未通过数")
    private int noPass;
    @ApiModelProperty("业务员数")
    private int salesman;
    @ApiModelProperty("今日业务员数")
    private int salesmanToday;
    @ApiModelProperty("今日审核数")
    private int alreadyApplyToday;
    @ApiModelProperty("今日未通过数")
    private int noPassToday;


}
