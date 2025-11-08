package top.jionjion.work.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.jionjion.work.service.PomodoroTimerService;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 番茄钟服务实现
 *
 * @author Jion
 */
@Slf4j
@Service
public class PomodoroTimerServiceImpl implements PomodoroTimerService {

    /**
     * 番茄钟时长(分钟)
     */
    private static final int POMODORO_MINUTES = 25;

    /**
     * 休息时长(分钟)
     */
    private static final int BREAK_MINUTES = 5;

    /**
     * 定时器
     */
    private Timer timer;

    /**
     * 剩余秒数
     */
    private int remainingSeconds;

    /**
     * 当前状态: IDLE(空闲), WORKING(工作中), BREAK(休息中), PAUSED(暂停)
     */
    private String currentState = "IDLE";

    /**
     * 是否正在运行
     */
    private boolean running = false;

    @Override
    public void start() {
        if (running && !"PAUSED".equals(currentState)) {
            log.warn("番茄钟已在运行中");
            return;
        }

        // 如果是第一次启动或从空闲状态启动
        if ("IDLE".equals(currentState)) {
            remainingSeconds = POMODORO_MINUTES * 60;
            currentState = "WORKING";
        } else if ("PAUSED".equals(currentState)) {
            // 从暂停恢复
            currentState = remainingSeconds > BREAK_MINUTES * 60 ? "WORKING" : "BREAK";
        }

        running = true;
        startTimer();
        log.info("番茄钟开始: {}", getStatus());
    }

    @Override
    public void pause() {
        if (!running) {
            return;
        }
        running = false;
        currentState = "PAUSED";
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        log.info("番茄钟暂停: {}", getStatus());
    }

    @Override
    public void stop() {
        running = false;
        currentState = "IDLE";
        remainingSeconds = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        log.info("番茄钟停止");
    }

    @Override
    public void reset() {
        stop();
        log.info("番茄钟重置");
    }

    @Override
    public String getStatus() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        return switch (currentState) {
            case "WORKING" -> String.format("🍅 工作中 %02d:%02d", minutes, seconds);
            case "BREAK" -> String.format("☕ 休息中 %02d:%02d", minutes, seconds);
            case "PAUSED" -> String.format("⏸ 已暂停 %02d:%02d", minutes, seconds);
            default -> "⏱ 番茄钟";
        };
    }

    @Override
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * 启动定时器
     */
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                } else {
                    // 时间到
                    onTimerFinished();
                }
            }
        }, 1000, 1000);
    }

    /**
     * 定时器结束处理
     */
    private void onTimerFinished() {
        timer.cancel();
        timer = null;

        if ("WORKING".equals(currentState)) {
            // 工作结束，进入休息
            showNotification("🍅 番茄钟完成！", "干得漂亮！休息 " + BREAK_MINUTES + " 分钟吧~");
            remainingSeconds = BREAK_MINUTES * 60;
            currentState = "BREAK";
            startTimer();
        } else if ("BREAK".equals(currentState)) {
            // 休息结束
            showNotification("☕ 休息结束", "准备好开始下一个番茄钟了吗？");
            running = false;
            currentState = "IDLE";
            remainingSeconds = 0;
        }
    }

    /**
     * 显示系统通知
     */
    private void showNotification(String title, String message) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon[] icons = tray.getTrayIcons();
            if (icons.length > 0) {
                icons[0].displayMessage(title, message, TrayIcon.MessageType.INFO);
            }
        }
        log.info("{} - {}", title, message);
    }
}
