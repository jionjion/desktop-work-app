package top.jionjion.work.service;

/**
 * 阿里云百炼 API
 *
 * @author Jion
 */
public interface BaiLianService {

    /**
     * 调用百炼-日报生成器
     *
     * @param string 提示词
     * @return 日报
     */
    String invokeDailyReport(String string);
}
