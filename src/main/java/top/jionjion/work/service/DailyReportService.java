package top.jionjion.work.service;

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

}