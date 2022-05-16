package com.px.pa.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.pig4cloud.pig.common.core.support.http.HttpCode;
import com.px.pa.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * API请求过滤器
 *
 * @author zhouz
 */
@WebFilter(urlPatterns = "/api/*", filterName = "ApiServiceFilter")
@Slf4j
public class ApiServiceFilter extends HttpFilter {
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //如果是登录操作
        if (StrUtil.containsAnyIgnoreCase(request.getRequestURL(), Constants.Auth.WX_LOGIN_PATH) || StrUtil.containsAnyIgnoreCase(request.getRequestURL(), Constants.Auth.WX_AUTO_LOGIN_PATH)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        //如果不是，则判断用户Token，如果没有报错401
        String token = request.getHeader(Constants.Auth.TOKEN_KEY);
        String h = request.getHeaderNames().nextElement().toLowerCase();
        log.info(h);
        log.info(Constants.Auth.TOKEN_KEY);
        log.info("token1================================================"+token);
        if (token == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", HttpCode.LOGIN_FAIL.value());
            servletResponse.getWriter().print(JSONUtil.toJsonStr(result));
            return;
        }

        Object idObj = this.redisHelper.hget(Constants.Auth.TOKEN_CACHE_KEY, token);
        log.info("toke2================================================"+idObj);
        if (idObj == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", HttpCode.LOGIN_FAIL.value());
            servletResponse.getWriter().print(JSONUtil.toJsonStr(result));
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
        return;
    }
}
