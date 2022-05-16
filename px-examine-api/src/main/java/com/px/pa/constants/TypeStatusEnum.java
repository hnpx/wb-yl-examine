package com.px.pa.constants;

import com.pig4cloud.pig.common.core.constant.enums.BaseEnum;

/**
 * 支付订单类型
 *
 * @author zhouz
 */
public enum TypeStatusEnum implements BaseEnum {

    TYPE_STATUS_ENUM_ONE(1, "今日审核"),
    TYPE_STATUS_ENUM_TWO(2, "今日未通过");

    private Integer value;
    private String desc;

    private TypeStatusEnum(Integer value, String desc) {
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
