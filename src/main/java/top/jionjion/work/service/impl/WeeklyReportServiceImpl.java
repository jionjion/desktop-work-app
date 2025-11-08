package top.jionjion.work.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.jionjion.work.entity.DailyReport;
import top.jionjion.work.entity.WeeklyReport;
import top.jionjion.work.repository.DailyReportRepository;
import top.jionjion.work.repository.WeeklyReportRepository;
import top.jionjion.work.service.BaiLianService;
import top.jionjion.work.service.WeeklyReportService;

import java.time.LocalDate;
import java.util.List;

/**
 * 自动生成周报的工具
 *
 * @author Jion
 */
@Slf4j
@Service
@AllArgsConstructor
public class WeeklyReportServiceImpl implements WeeklyReportService {

    private final BaiLianService baiLianService;

    private final WeeklyReportRepository weeklyReportRepository;

    private final DailyReportRepository dailyReportRepository;

    /**
     * 自动生成周报
     */
    @Override
    public String generateReport(LocalDate startDate, LocalDate endDate) {

        StringBuilder stringBuilder = new StringBuilder("以下为本周的日报信息\n");
        stringBuilder.append("--------------------------\n");

        // 获取本周的日报, 根据日期拼接
        List<DailyReport> dailyReports = dailyReportRepository.findByReportDateBetweenOrderByReportDateAsc(startDate, endDate);

        if (dailyReports.isEmpty()) {
            stringBuilder.append("本周暂无日报记录\n");
        } else {
            for (DailyReport dailyReport : dailyReports) {
                stringBuilder.append("\n");
                stringBuilder.append("标题: ").append(dailyReport.getTitle()).append("\n");
                stringBuilder.append("内容: \n").append(dailyReport.getContent()).append("\n");
                stringBuilder.append("--------------------------\n");
            }
        }

        // 调用阿里云服务, 生成周报格式
        return baiLianService.invokeWeeklyReport(stringBuilder.toString());
    }

    @Override
    public WeeklyReport findByYearAndWeekOfYear(Integer year, Integer weekOfYear) {
        return weeklyReportRepository.findByReportYearAndWeekOfYear(year, weekOfYear);
    }

    @Override
    public WeeklyReport findByStartDate(LocalDate startDate) {
        return weeklyReportRepository.findByStartDate(startDate);
    }

    @Override
    public WeeklyReport save(WeeklyReport weeklyReport) {
        return weeklyReportRepository.save(weeklyReport);
    }
}
