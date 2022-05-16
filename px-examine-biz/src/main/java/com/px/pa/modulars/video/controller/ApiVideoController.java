package com.px.pa.modulars.video.controller;

import cn.hutool.json.JSONUtil;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.video.vo.LivePushParam;
import com.px.pa.visual.file.VideoFileHelper;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.MediaInfo;
import com.tencentcloudapi.vod.v20180717.models.SearchMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.SearchMediaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 视频服务接口
 */
@RestController()
@RequestMapping("/video")
public class ApiVideoController {
    @Autowired
    private VideoFileHelper videoFileHelper;
    @Autowired
    private ExamineTaskService examineTaskService;
    @Autowired
    private VodClient client;

    /**
     * 视频关闭后的通知消息
     * TODO 缺少异常情况处理，如果出现异常情况需要有补偿机制，处理
     *
     * @return
     */
    @Inner(false)
    @RequestMapping("/live-push")
    public Object endNotify(@RequestBody LivePushParam param) {

        System.out.println("================================接收到视频回调==============================");
        System.out.println(JSONUtil.toJsonStr(param));
        System.out.println("================================接收到视频回调==============================");

        this.examineTaskService.liveEnd(param);
//        return R.ok();
        return "success";
    }

    @Inner(false)
    @GetMapping("/search/{key}")
    public Object demo(@PathVariable("key") String key) {

        SearchMediaRequest req = new SearchMediaRequest();
        String[] params = new String[]{key};
        req.set("Names", params);
        req.setLimit(1000L);
        StringBuilder sb = new StringBuilder();
        try {
            SearchMediaResponse resp = this.client.SearchMedia(req);
            MediaInfo[] infos = resp.getMediaInfoSet();
            for (MediaInfo info : infos) {
                sb.append(info.getFileId() + " \t" + info.getBasicInfo().getName() + "\t" + info.getBasicInfo().getMediaUrl() + "\n");
            }
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        return R.ok(sb.toString());
    }

}
