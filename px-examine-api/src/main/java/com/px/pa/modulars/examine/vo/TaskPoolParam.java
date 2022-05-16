package com.px.pa.modulars.examine.vo;

import com.px.pa.modulars.examine.entity.ExamineTask;
import lombok.Data;

@Data
public class TaskPoolParam extends ExamineTask {
    private Integer pageNum;
    private Integer pageSize;
}
