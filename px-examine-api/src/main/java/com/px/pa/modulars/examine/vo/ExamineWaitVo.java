package com.px.pa.modulars.examine.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamineWaitVo {
    private LocalDateTime applyTime;
    private String shopName;
    private String shopAddress;
    private String userName;
    private String phone;
    private Integer shop;
    private Integer examineTask;
    private Integer examineType;
}
