package com.px.pa.modulars.examine.vo;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 视频信息
 */
@Data
@ToString
public class VideoInfo {
    private String url;
    private String startTime;
    private String endTime;
    private int duration;
    private String fileId;
    private boolean enable;

    public VideoInfo() {
    }

    public VideoInfo(String url, String startTime, String endTime, int duration, String fileId) {
        if (url != null && url.startsWith("http://")) {
            url = "https://" + url.substring(7);
        }
        this.url = url;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.fileId = fileId;
        this.enable = true;
    }

    public VideoInfo(String url, long startTime, long endTime, int duration, String fileId) {
        if (url != null && url.startsWith("http://")) {
            url = "https://" + url.substring(7);
        }
        this.url = url;
        this.startTime = this.timeToStr(startTime * 1000);
        this.endTime = this.timeToStr(endTime * 1000);
        this.duration = duration;
        this.fileId = fileId;
        this.enable = true;
    }

    private String timeToStr(long time) {
        return DateUtil.format(new Date(time), "yyyy-MM-dd HH:mm:ss");
    }
}
