package com.px.pa.modulars.shop.shopUser.vo;

import com.px.pa.constants.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@ApiModel("登录参数")
public class LoginParam {
    @ApiModelProperty("手机号")
    @NotNull(message = "请填写手机号")
    @Max(value = 11, message = "请填写准确的手机号")
    @Min(value = 11, message = "请填写准确的手机号")
    private String phone;
    @ApiModelProperty("短信验证码")
    @Max(value = Constants.LOGIN_SMS_CODE_LENGTH, message = "请填写准确的短信验证码")
    @Min(value = Constants.LOGIN_SMS_CODE_LENGTH, message = "请填写准确的短信验证码")
    private String code;
}
