package com.px.pa.modulars.examine.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamineInfoVo {
    private LocalDateTime createTime;
    private LocalDateTime applyTime;
    private LocalDateTime actualApplyTime;
    private LocalDateTime videoStarttime;
    private LocalDateTime videoEndtime;
    private String name;
}
