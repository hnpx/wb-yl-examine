package com.px.pa.modulars.examine.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class ExamineStatusVo {
    @Excel(name = "符合",width = 6)
    private String pass;
    @Excel(name = "不符合",width = 6)
    private String nopass;
    @Excel(name = "限期整改",width = 6)
    private String change;
}
