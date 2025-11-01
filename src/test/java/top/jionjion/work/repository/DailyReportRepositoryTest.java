package top.jionjion.work.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.jionjion.work.BaseTest;
import top.jionjion.work.entity.DailyReport;

/**
 * @author Jion
 */
class DailyReportRepositoryTest extends BaseTest {

    @Autowired
    DailyReportRepository dailyReportRepository;

    @Test
    void findAll() {
        System.out.println(dailyReportRepository.findAll());

        System.out.println("-------------------------");

        DailyReport dailyReport = new DailyReport();
        dailyReport.setTitle("测试");
        dailyReport.setContent("测试");
        dailyReportRepository.save(dailyReport);
        System.out.println(dailyReportRepository.findAll());
    }
}