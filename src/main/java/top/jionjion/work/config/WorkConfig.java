package top.jionjion.work.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 工作配置类
 * 用于读取config/work.properties配置文件
 *
 * @author Jion
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "work")
public class WorkConfig {

    /**
     * 应用程序名称
     */
    private String appName;

    /**
     * 应用程序版本
     */
    private String appVersion;
    
}
