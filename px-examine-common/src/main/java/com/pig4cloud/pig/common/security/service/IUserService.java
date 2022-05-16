package com.pig4cloud.pig.common.security.service;

import com.px.pa.modulars.upms.dto.UserInfo;
import com.pig4cloud.pig.common.core.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IUserService {
   public UserInfo readInfo( String username);
//
//    @GetMapping({"/social/info/{inStr}"})
//    R<UserInfo> social(@PathVariable("inStr") String var1);
}
