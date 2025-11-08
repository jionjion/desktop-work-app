package top.jionjion.work.service;

import java.time.LocalDate;
import java.util.List;

import top.jionjion.work.entity.CodeDirectory;
import top.jionjion.work.entity.DailyReport;

/**
 * 日报服务类
 *
 * @author Jion
 */
public interface DailyReportService {

    /**
     * 生成日报
     * 
     * @return 日报内容
     */
    String generateReport();

    /**
     * 获取日报配置
     *
     * @return 日报配置
     */
    List<CodeDirectory> findAllCodeDirectory();

    /**
     * 保存今日日报
     *
     * @param content 日报内容
     * @return 日报
     */
    DailyReport seveTodayReport(String content);

    /**
     * 根据日期查询日报
     *
     * @param reportDate 日报日期
     * @return 日报
     */
    DailyReport findByReportDate(LocalDate reportDate);

    /**
     * 保存或更新指定日期的日报
     *
     * @param reportDate 日报日期
     * @param content 日报内容
     * @return 日报
     */
    DailyReport saveOrUpdateReport(LocalDate reportDate, String content);

}