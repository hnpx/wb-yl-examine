package com.px.pa.modulars.examine.vo;

import lombok.Data;

@Data
public class TaskCountInfo {
    /**
     * 总数
     */
    private Integer total;
    /**
     * 未通过
     */
    private Integer failed;
    /**
     * 已通过
     */
    private Integer adopt;
    /**
     * 等待中
     */
    private Integer wait;



}
