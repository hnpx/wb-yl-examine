package com.px.pa.config.tencent;

import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.vod.v20180717.VodClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
// 导入对应产品模块的 client
import com.tencentcloudapi.cvm.v20170312.CvmClient;
// 导入要请求接口对应的 request response 类
import com.tencentcloudapi.cvm.v20170312.models.DescribeZonesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeZonesResponse;
//导入可选配置类
import com.tencentcloudapi.common.profile.HttpProfile;

@Configuration
public class TrtcConfiguration {
    @Autowired
    private TrtcProperties trtcProperties;

    /**
     * 业务根
     *
     * @return
     */
    @Bean
    public Credential getCredential() {
        Credential cred = new Credential(this.trtcProperties.getSecretId(), this.trtcProperties.getSecretKey());
        return cred;
    }

    /**
     * 视频点播业务
     *
     * @param credential
     * @return
     */
    @Bean
    public VodClient getVodClient(Credential credential) {
        return new VodClient(credential, this.trtcProperties.getRegion());
    }

    @Bean
    public TrtcClient getTrtcClient(Credential credential) {
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new TrtcClient(credential, this.trtcProperties.getRegion(), clientProfile);
    }

}
