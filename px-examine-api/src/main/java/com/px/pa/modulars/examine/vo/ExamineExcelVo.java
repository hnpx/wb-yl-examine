package com.px.pa.modulars.examine.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.List;

@Data
public class ExamineExcelVo {
    @Excel(name = "任务分类",width = 20)
    private String tasktypeName;
    @Excel(name = "一级标题",width = 20)
    private String totle;
    @Excel(name = "审批项",width = 20)
    private String tasktypeItemName;
    @Excel(name = "状态",width = 20)
    private String status;
    @Excel(name = "证明材料",width = 20)
    private String img;

}
