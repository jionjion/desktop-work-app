package top.jionjion.work.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import top.jionjion.work.BaseTest;
import top.jionjion.work.service.impl.DailyReportServiceImpl;

/**
 * 测试生成日报
 *
 * @author Jion
 */
class DailyReportServiceTest extends BaseTest {

    @Autowired
    DailyReportServiceImpl dailyReportService;

    @Test
    @Rollback(false)
    void generateReport() {
        String report = dailyReportService.generateReport();
        System.out.println(report);
    }
}