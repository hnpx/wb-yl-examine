package com.pig4cloud.pig.common.security.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.px.pa.modulars.SysConfigSms.entity.SysConfigSms;
import com.px.pa.modulars.SysConfigSms.mapper.SysConfigSmsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jipfq
 */
@Service("sendSmsService")

public class SendSmsService {
    @Resource
    private SysConfigSmsMapper sysConfigSmsMapper;

    //	private Aliyun aliyun;
    private DefaultProfile profile;
    private IAcsClient client;
    private SysConfigSms sysConfigSms;
    public void initConfig(){
      sysConfigSms = sysConfigSmsMapper.selectById(1L);
//		BeanUtil.copyProperties(sysConfigSmsEntity1,aliyun);
//		this.aliyun = aliyun;
        this.profile = DefaultProfile.getProfile(
                "cn-hangzhou",
                this.sysConfigSms.getAccesskeyid(),
                this.sysConfigSms.getAccesskeysecret());
        this.client = new DefaultAcsClient(profile);
    }

    public Map<String,String> sendSms(String phone, String temp, Map<String,String> param){
        initConfig();
        CommonRequest request=new CommonRequest();
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers",phone);
        request.putQueryParameter("SignName", this.sysConfigSms.getSignname());
        request.putQueryParameter("TemplateCode", temp);
        if(param!=null){
            JSONObject obj= (JSONObject) JSONObject.toJSON(param);
            request.putQueryParameter("TemplateParam", obj.toJSONString());
        }
        CommonResponse commonResponse = null;
        Map<String,String> result=new HashMap<>();
        try {
            commonResponse = client.getCommonResponse(request);
            String data = commonResponse.getData();
            String sData = data.replaceAll("'\'", "");
            Gson gson = new Gson();
            Map map = gson.fromJson(sData, Map.class);
            if(map.get("Message").toString().equals("OK")&&map.get("Code").equals("OK")){
                result.put("success", "1");
            }else{
                result.put("success", "0");
                result.put("msg", map.get("Message").toString());
            }
        } catch (ClientException e) {
            e.printStackTrace();
            result.put("msg", e.getErrorDescription());
            result.put("success", "0");
        }
        return result;
    }
}
