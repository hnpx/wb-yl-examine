/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pig4cloud.pig.common.security.feign;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.security.oauth2.client.AccessTokenContextRelay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * @author lengleng
 * @date 2019/2/1 feign 拦截器传递 header 中oauth token， 使用hystrix 的信号量模式
 */
@Configuration
@ConditionalOnProperty("security.oauth2.client.client-id")
public class PigFeignClientConfiguration {

	@Bean
	public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext,
			OAuth2ProtectedResourceDetails resource, AccessTokenContextRelay accessTokenContextRelay) {
		return new PigFeignClientInterceptor(oAuth2ClientContext, resource, accessTokenContextRelay);
	}

}
