package com.px.pa.constants;

import com.pig4cloud.pig.common.core.constant.enums.BaseEnum;

/**
 * 支付订单类型
 *
 * @author zhouz
 */
public enum ExamineItemStatusEnum implements BaseEnum {

    EXAMINE_STATUS_ENUM_ONE(1, "符合"),
    EXAMINE_STATUS_ENUM_TWO(2, "不符合"),
    EXAMINE_STATUS_ENUM_THREE(3, "限期整改");

    private Integer value;
    private String desc;

    private ExamineItemStatusEnum(Integer value, String desc) {
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
