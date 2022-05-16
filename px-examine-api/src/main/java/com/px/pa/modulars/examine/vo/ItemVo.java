package com.px.pa.modulars.examine.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemVo {

    private String totle;

    private String info;

    private String example;

    private String standard;

    private Integer tasktype;

    private Integer createBy;

    private LocalDateTime createTime;

    private Integer updateBy;

    private LocalDateTime updateTime;

    private Integer enable;

    private String remark;

    private Integer sort;

    private Integer id;
}
