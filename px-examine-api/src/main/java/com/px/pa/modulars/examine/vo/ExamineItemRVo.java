package com.px.pa.modulars.examine.vo;

import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import lombok.Data;

import java.util.List;

@Data
public class ExamineItemRVo {
    private String name;
    private List<ExamineTaskItem> examineTaskItemList;
}
