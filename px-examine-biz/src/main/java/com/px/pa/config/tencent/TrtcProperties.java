package com.px.pa.config.tencent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实时音视频配置信息
 */
@Data
@ConfigurationProperties(prefix = "tencent.trtc")
public class TrtcProperties {
    private String secretId;
    private String secretKey;
    private String secretAppid;
    private String appId;

    /**
     * 业务区域
     */
    private String region;

}
