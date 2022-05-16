package com.px.pa.modulars.SysConfigSms.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Aliyun {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String regionId;
    public static String VERIFY_CODE ="SMS_198790181";
}
