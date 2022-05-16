package com.px.pa.constants;

import com.pig4cloud.pig.common.core.constant.enums.BaseEnum;

/**
 * 支付订单类型
 *
 * @author zhouz
 */
public enum TypeEnum implements BaseEnum {

    TYPE_ENUM_ONE(1, "现场踏勘"),
    TYPE_ENUM_TWO(2, "远程踏勘");

    private Integer value;
    private String desc;

    private TypeEnum(Integer value, String desc) {
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
