package top.jionjion.work.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云百炼配置
 *
 * @author Jion
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bai-lian")
public class BaiLianConfig {

    /**
     * 百炼的API-KEY, 是调用平台的基础配置
     */
    private String apiKey;

    /**
     * 日报生成应用的ID
     */
    private String dailyReportAppId;

}
