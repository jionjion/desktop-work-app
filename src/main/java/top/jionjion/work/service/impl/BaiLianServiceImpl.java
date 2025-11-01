package top.jionjion.work.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.jionjion.work.config.BaiLianConfig;
import top.jionjion.work.entity.BaiLianLog;
import top.jionjion.work.exception.AppException;
import top.jionjion.work.repository.BaiLianLogRepository;
import top.jionjion.work.service.BaiLianService;

/**
 * 阿里云百炼 API
 *
 * @author Jion
 */
@Slf4j
@Service
@AllArgsConstructor
public class BaiLianServiceImpl implements BaiLianService {

    private final BaiLianConfig baiLianConfig;

    private final BaiLianLogRepository baiLianLogRepository;

    /**
     * 调用阿里云百炼应用
     *
     * @param appId  应用ID
     * @param prompt 用户输入提示词
     */
    public String doInvokeBaiLian(String appId, String prompt) {
        BaiLianLog logEntry = new BaiLianLog();
        logEntry.setAppId(appId);
        logEntry.setPrompt(prompt);
        logEntry.setInvokeTime(java.time.LocalDateTime.now());

        ApplicationParam param = ApplicationParam.builder().apiKey(baiLianConfig.getApiKey())
                .appId(appId).prompt(prompt).build();
        Application application = new Application();
        try {
            ApplicationResult result = application.call(param);
            String output = result.getOutput().getText();

            // 记录成功日志
            logEntry.setResult(output);
            logEntry.setSuccess(true);
            baiLianLogRepository.save(logEntry);

            return output;
        } catch (NoApiKeyException e) {
            // 记录错误日志
            logEntry.setSuccess(false);
            logEntry.setErrorMessage("API密钥异常: " + e.getMessage());
            baiLianLogRepository.save(logEntry);

            throw new AppException("API密钥异常", e);
        } catch (InputRequiredException e) {
            // 记录错误日志
            logEntry.setSuccess(false);
            logEntry.setErrorMessage("输入参数异常: " + e.getMessage());
            baiLianLogRepository.save(logEntry);

            throw new AppException("输入参数异常", e);
        } catch (Exception e) {
            // 记录其他异常
            logEntry.setSuccess(false);
            logEntry.setErrorMessage("未知异常: " + e.getMessage());
            baiLianLogRepository.save(logEntry);

            throw new AppException("调用百炼平台异常", e);
        }
    }

    @Override
    public String invokeDailyReport(String string) {
        String result = doInvokeBaiLian(baiLianConfig.getDailyReportAppId(), string);
        // 去除响应结果中可能存在的代码块标记
        if (result.startsWith("```") && result.endsWith("```")) {
            result = result.substring(3, result.length() - 3).trim();
        }
        return result;
    }
}
