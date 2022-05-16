package com.px.pa.visual.file;

import cn.hutool.http.HttpUtil;
import com.px.fastfile.service.FastfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class VideoFileHelper {

    @Value("${file.temporary}")
    private String temporary;

    @Autowired
    private FastfileService fastfileService;

    /**
     * 使用
     *
     * @param url
     */
    public void uploadByUrl(String url, String id) {
        //文件类型固定是MP4
        String filePath = this.temporary + File.separator + id + ".mp4";
        HttpUtil.downloadFileFromUrl(url, filePath);

    }

}
