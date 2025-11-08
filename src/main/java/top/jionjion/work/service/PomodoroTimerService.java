package top.jionjion.work.service;

/**
 * 番茄钟服务接口
 *
 * @author Jion
 */
public interface PomodoroTimerService {

    /**
     * 开始番茄钟
     */
    void start();

    /**
     * 暂停番茄钟
     */
    void pause();

    /**
     * 停止番茄钟
     */
    void stop();

    /**
     * 重置番茄钟
     */
    void reset();

    /**
     * 获取当前状态
     *
     * @return 状态描述
     */
    String getStatus();

    /**
     * 获取剩余时间(秒)
     *
     * @return 剩余时间
     */
    int getRemainingSeconds();

    /**
     * 是否正在运行
     *
     * @return true-运行中
     */
    boolean isRunning();
}
