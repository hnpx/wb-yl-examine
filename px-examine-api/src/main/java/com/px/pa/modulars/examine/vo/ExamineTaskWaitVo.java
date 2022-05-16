package com.px.pa.modulars.examine.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExamineTaskWaitVo {
    private int count;
    private List<ExamineWaitVo> examineWaitVoList;
}
