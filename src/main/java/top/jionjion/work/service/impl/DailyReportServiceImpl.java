package top.jionjion.work.service.impl;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.jionjion.work.entity.CodeDirectory;
import top.jionjion.work.entity.DailyReport;
import top.jionjion.work.repository.CodeDirectoryRepository;
import top.jionjion.work.repository.DailyReportRepository;
import top.jionjion.work.service.BaiLianService;
import top.jionjion.work.service.DailyReportService;

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

        StringBuilder stringBuilder = new StringBuilder();

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
    public DailyReport seveTodayReport(String content) {
        DailyReport dailyReport = new DailyReport();
        String title = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-日报";
        dailyReport.setTitle(title);
        dailyReport.setContent(content);
        dailyReportRepository.save(dailyReport);
        return dailyReport;
    }
}
