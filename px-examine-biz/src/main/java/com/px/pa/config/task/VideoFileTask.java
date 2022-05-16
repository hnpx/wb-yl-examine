package com.px.pa.config.task;

import com.px.pa.modulars.examine.service.ExamineVideoBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件任务定时器
 */
@Component
public class VideoFileTask {

    @Autowired
    private ExamineVideoBinService videoBinService;

    /**
     * 文件过期处理，每三十分钟调用一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
//    @Scheduled(cron = "*/5 * * * * ?")
    public void execuFileOverdueHandle() {
        this.videoBinService.delOverdueFiles();
    }
}
