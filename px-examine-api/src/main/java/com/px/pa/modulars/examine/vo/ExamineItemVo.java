package com.px.pa.modulars.examine.vo;

import com.px.pa.modulars.base.entity.BaseTasktypeItem;
import lombok.Data;

import java.util.List;

@Data
public class ExamineItemVo {
    private String name;
    private List<BaseTasktypeItem> baseTasktypeItemList;
}
