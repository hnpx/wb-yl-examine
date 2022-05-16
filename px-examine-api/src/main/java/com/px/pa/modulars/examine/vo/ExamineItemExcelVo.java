package com.px.pa.modulars.examine.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class ExamineItemExcelVo {
    @Excel(name = "核查项目",width = 20)
    private String totle;
    @Excel(name = "序号",width = 10)
    private Integer number;
    @Excel(name="核查内容和核查标准",width = 30)
    private String info;
    @Excel(name = "评价意见",width = 20)
    private String opinion;
}
