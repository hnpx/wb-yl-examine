package com.px.pa.modulars.video.vo;

import com.tencentcloudapi.trtc.v20190722.models.EncodeParams;
import com.tencentcloudapi.trtc.v20190722.models.LayoutParams;
import com.tencentcloudapi.trtc.v20190722.models.OutputParams;
import lombok.Data;

@Data
public class MixedioVo {

    private String Action;
    private String Version;
    private String Region;
    private Integer SdkAppId;
    private Integer RoomId;
    private OutputParams outputParams;
    private EncodeParams encodeParams;
    private LayoutParams layoutParams;

}
