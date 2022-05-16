package com.px.pa.modulars.examine.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.function.IntUnaryOperator;

@Data
public class ExamineTaskBySysTemVo {

    private String rowguid;
    private Integer tasktype;
    private String shopName;
    private String shopNum;
    private String shopAddress;
    private String userName;
    private String shopTel;
    private String idCard;
    private LocalDateTime applyTime;
}
