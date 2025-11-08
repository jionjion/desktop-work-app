package top.jionjion.work.service;

import top.jionjion.work.entity.WeeklyReport;

import java.time.LocalDate;

/**
 * 周报服务类
 *
 * @author Jion
 */
public interface WeeklyReportService {

    /**
     * 生成周报
     * 
     * @param startDate 周开始日期
     * @param endDate 周结束日期
     * @return 周报内容
     */
    String generateReport(LocalDate startDate, LocalDate endDate);


    /**
     * 根据年份和周数查询周报
     *
     * @param year 年份
     * @param weekOfYear 第几周
     * @return 周报
     */
    WeeklyReport findByYearAndWeekOfYear(Integer year, Integer weekOfYear);

    /**
     * 根据开始日期查询周报
     *
     * @param startDate 开始日期
     * @return 周报
     */
    WeeklyReport findByStartDate(LocalDate startDate);

    /**
     * 保存或更新周报
     *
     * @param weeklyReport 周报实体
     * @return 周报
     */
    WeeklyReport save(WeeklyReport weeklyReport);
}
