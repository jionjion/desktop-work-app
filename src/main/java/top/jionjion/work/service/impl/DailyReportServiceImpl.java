package top.jionjion.work.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.jionjion.work.entity.CodeDirectory;
import top.jionjion.work.entity.DailyReport;
import top.jionjion.work.repository.CodeDirectoryRepository;
import top.jionjion.work.repository.DailyReportRepository;
import top.jionjion.work.service.BaiLianService;
import top.jionjion.work.service.DailyReportService;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 自动生成日报的工具
 *
 * @author Jion
 */
@Slf4j
@Service
@AllArgsConstructor
public class DailyReportServiceImpl implements DailyReportService {

    private final GitServiceImpl gitService;

    private final BaiLianService baiLianService;

    private final DailyReportRepository dailyReportRepository;

    private final CodeDirectoryRepository codeDirectoryRepository;

    /**
     * 自动生成日报
     */
    @Override
    public String generateReport() {

        StringBuilder stringBuilder = new StringBuilder("以下为代码提交记录\n");
        stringBuilder.append("--------------------------");

        // 获取当前负责的项目的地址.
        List<CodeDirectory> codeDirectoryList = this.findAllCodeDirectory();

        // 获取今日的代码提交
        for (CodeDirectory directory : codeDirectoryList) {
            // 获取最后一个文件夹的名字
            String result = gitService.getTodayGitCommits(directory.getPath());
            String template = "项目: {0}\n今日提交记录: \n{1}\n";
            String formatted = MessageFormat.format(template, directory.getName(), result);
            stringBuilder.append(formatted);
        }

        // 调用阿里云服务, 生成日报格式
        return baiLianService.invokeDailyReport(stringBuilder.toString());
    }

    @Override
    public List<CodeDirectory> findAllCodeDirectory() {
        return codeDirectoryRepository.findAll();
    }

    @Override
    @Transactional
    public DailyReport seveTodayReport(String content) {
        LocalDate today = LocalDate.now();

        // 先删除今天的日报
        dailyReportRepository.deleteByReportDate(today);

        // 再新增今天的日报
        DailyReport dailyReport = new DailyReport();
        String title = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-日报";
        dailyReport.setTitle(title);
        dailyReport.setContent(content);
        dailyReport.setReportDate(today);
        dailyReportRepository.save(dailyReport);
        return dailyReport;
    }

    @Override
    public DailyReport findByReportDate(LocalDate reportDate) {
        return dailyReportRepository.findByReportDate(reportDate);
    }

    @Override
    @Transactional
    public DailyReport saveOrUpdateReport(LocalDate reportDate, String content) {
        // 查询是否已存在该日期的日报
        DailyReport existingReport = dailyReportRepository.findByReportDate(reportDate);
        
        if (existingReport != null) {
            // 更新已存在的日报
            existingReport.setContent(content);
            return dailyReportRepository.save(existingReport);
        } else {
            // 创建新日报
            DailyReport newReport = new DailyReport();
            String title = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-日报";
            newReport.setTitle(title);
            newReport.setContent(content);
            newReport.setReportDate(reportDate);
            return dailyReportRepository.save(newReport);
        }
    }
}
