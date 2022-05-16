package com.px.pa.modulars.video.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * 视频推送参数
 */
@Data
@ToString
@ApiModel("视频推送参数")
public class LivePushParam {
    private String app;
    private String appid;
    private String appname;
    private String event_type;
    private String file_size;
    private String file_format;
    private String file_id;
    private String stream_id;
    private String video_id;
    private String video_url;
    private String start_time;
    private String end_time;
    private String duration;
    /**
     * 文件ID，删除时候使用
     */
    private String record_file_id;
    private Integer taskId;
}
