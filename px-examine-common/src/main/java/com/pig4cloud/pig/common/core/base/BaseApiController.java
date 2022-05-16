package com.pig4cloud.pig.common.core.base;

import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.px.pa.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
@Slf4j
public class BaseApiController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected RedisHelper redisHelper;

    /**
     * 获取当前用户的ID
     *
     * @return
     */
    protected Integer getCurrentId() {
        String token = request.getHeader(Constants.Auth.TOKEN_KEY);
        if (token == null) {
            return null;
        }
        Object idObj = this.redisHelper.hget(Constants.Auth.TOKEN_CACHE_KEY, token);
        log.info("=============================================="+idObj);
        return idObj == null ? null : Integer.parseInt(idObj.toString());
    }

}
