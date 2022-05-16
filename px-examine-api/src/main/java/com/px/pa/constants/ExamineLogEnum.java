package com.px.pa.constants;

import com.pig4cloud.pig.common.core.constant.enums.BaseEnum;

/**
 * 支付订单类型
 *
 * @author zhouz
 */
public enum ExamineLogEnum implements BaseEnum {

    EXAMINE_STATUS_ENUM_ONE(1, "管理员进入房间"),
    EXAMINE_STATUS_ENUM_TWO(2, "商家进入房间"),
    EXAMINE_STATUS_ENUM_THREE(3, "视频挂断结束"),
    EXAMINE_STATUS_ENUM_FOUR(4, "提交审核意见");

    private Integer value;
    private String desc;

    private ExamineLogEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
