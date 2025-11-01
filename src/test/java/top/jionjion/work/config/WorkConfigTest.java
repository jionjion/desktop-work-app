package top.jionjion.work.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import top.jionjion.work.BaseTest;

/**
 * 测试配置文件读取功能
 *
 * @author Jion
 */
class WorkConfigTest extends BaseTest {

    // 测试配置文件读取功能
    @Autowired
    WorkConfig config;

    @Test
    void getAppName() {
        System.out.println("应用名称: " + config.getAppName());
    }

    @Test
    void getAppVersion() {
        System.out.println("应用版本: " + config.getAppVersion());
    }
}