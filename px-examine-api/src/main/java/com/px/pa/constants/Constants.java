package com.px.pa.constants;

/**
 * 常量
 */
public interface Constants {
    public interface Auth {
        public String WX_LOGIN_PATH = "/api/wx/login";
        public String WX_AUTO_LOGIN_PATH = "/api/shop/default/login";
        /**
         * tokenKey
         */
        public String TOKEN_KEY = "TAKAN_TOKEN";
        //public String TOKEN_KEY = "takan_token";
        public String TOKEN_CACHE_KEY = "TAKAN_CACHE_TOKEN";
        public Integer TOKEN_KEY_TIMEOUT = 1000;
    }

    /**
     * 短信验证码
     */
    public String LOGIN_SMS_CODE = "LOGIN_SMS_CODE";

    public int LOGIN_SMS_CODE_LENGTH = 6;
    /**
     * 短信验证码的过期时间
     */
    public int LOGIN_SMS_OUTTIME = 120;

    /**
     * 房间管理员开启标志
     */
    public String ROOM_ADMIN_FLAG = "ROOM_ADMIN_FLAG";
    /**
     * 房间管理员开启标志的过期时间，秒
     */
    public int ROOM_ADMIN_FLAG_OUTTIME = 60;
    /**
     * 审核结果，待审核
     */
    public int TASK_STATUS_WAIT = 1;

    /**
     * 审核结果，待审核
     */
    public int TASK_STATUS_SUCCESS = 2;

    /**
     * 审核结果，待审核
     */
    public int TASK_STATUS_ERROR = 3;

    /**
     * 业务员身份
     */
    public int ROLE_YW = 6;
    /**
     * 超级管理员身份
     */
    public int ROLE_CJ = 1;
    /**
     * 审核视频删除状态-废弃
     */
    public int TASK_VIDEO_BIN_STATUS_DIS = 1;
    /**
     * 审核视频删除状态-删除
     */
    public int TASK_VIDEO_BIN_STATUS_DEL = 2;
    /**
     * 审核视频删除状态-还原
     */
    public int TASK_VIDEO_BIN_STATUS_RED = 3;
    /**
     * 用户在线信息
     */
    public String USER_ONLINE_INFO = "USER_ONLINE_INFO";
    /**
     * 30秒刷新
     */
    public int USER_ONLINE_INFO_TIMEOUT = 30000;
}
