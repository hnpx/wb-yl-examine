package com.px.pa.constants;

import com.pig4cloud.pig.common.core.constant.enums.BaseEnum;

/**
 * 支付订单类型
 *
 * @author zhouz
 */
public enum ExamineStatusEnum implements BaseEnum {

    EXAMINE_STATUS_ENUM_ONE(1, "待审核"),
    EXAMINE_STATUS_ENUM_TWO(2, "已通过"),
    EXAMINE_STATUS_ENUM_THREE(3, "未通过");

    private Integer value;
    private String desc;

    private ExamineStatusEnum(Integer value, String desc) {
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
