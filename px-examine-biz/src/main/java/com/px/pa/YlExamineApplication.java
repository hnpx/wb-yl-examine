package com.px.pa;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.px.fastfile.config.FastfileConfig;
import com.px.msg.config.propertis.MessageProperties;
import com.px.pa.config.tencent.TrtcProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author pig archetype
 * <p>
 * 项目启动类
 */
@EnablePigResourceServer
@ServletComponentScan
@SpringBootApplication
@ComponentScan(basePackages = {"com.px", "com.pig4cloud.pig"})
@EnableConfigurationProperties({FastfileConfig.class, TrtcProperties.class, MessageProperties.class})
@EnableScheduling
public class YlExamineApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(YlExamineApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
