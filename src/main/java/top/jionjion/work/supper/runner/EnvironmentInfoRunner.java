package top.jionjion.work.supper.runner;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 获取环境信息
 *
 * @author Jion
 */
@Slf4j
@Component
public class EnvironmentInfoRunner implements EnvironmentAware, ApplicationRunner {

    private Environment environment;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("------------------ 环境信息 ------------------");
        log.info("当前激活的配置文件: {}", String.join(", ", environment.getActiveProfiles()));
        log.info("数据库连接地址: {}", environment.getProperty("spring.datasource.url"));
        log.info("当前环境: {}", environment.getProperty("spring.profiles.active"));
        log.info("---------------------------------------------");
    }
}
