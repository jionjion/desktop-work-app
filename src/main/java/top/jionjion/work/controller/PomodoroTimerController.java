package top.jionjion.work.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jionjion.work.service.PomodoroTimerService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 番茄钟控制器
 *
 * @author Jion
 */
@Slf4j
@Component
public class PomodoroTimerController {

    @FXML
    private Label timeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button startBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Label completedCountLabel;

    @FXML
    private Label workTimeLabel;

    @Autowired
    private PomodoroTimerService pomodoroTimerService;

    /**
     * UI更新定时器
     */
    private Timer uiUpdateTimer;

    /**
     * 今日完成数
     */
    private int completedCount = 0;

    /**
     * 今日工作时长(分钟)
     */
    private int totalWorkMinutes = 0;

    @FXML
    private void initialize() {
        log.info("番茄钟界面初始化");
        startUIUpdateTimer();
    }

    /**
     * 开始番茄钟
     */
    @FXML
    private void handleStart() {
        pomodoroTimerService.start();
        updateButtonStates(true);
    }

    /**
     * 暂停番茄钟
     */
    @FXML
    private void handlePause() {
        pomodoroTimerService.pause();
        updateButtonStates(false);
    }

    /**
     * 停止番茄钟
     */
    @FXML
    private void handleStop() {
        pomodoroTimerService.stop();
        updateButtonStates(false);
    }

    /**
     * 更新按钮状态
     */
    private void updateButtonStates(boolean running) {
        startBtn.setDisable(running);
        pauseBtn.setDisable(!running);
        stopBtn.setDisable(!running);
    }

    /**
     * 启动UI更新定时器
     */
    private void startUIUpdateTimer() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.cancel();
        }

        uiUpdateTimer = new Timer();
        uiUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            private String lastStatus = "";
            
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // 更新时间显示
                    int remaining = pomodoroTimerService.getRemainingSeconds();
                    int minutes = remaining / 60;
                    int seconds = remaining % 60;
                    timeLabel.setText(String.format("%02d:%02d", minutes, seconds));

                    // 更新状态和颜色
                    String status = pomodoroTimerService.getStatus();
                    
                    if (status.contains("工作中")) {
                        statusLabel.setText("🍅 专注工作中");
                        timeLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
                    } else if (status.contains("休息中")) {
                        statusLabel.setText("☕ 休息时间");
                        timeLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                    } else if (status.contains("已暂停")) {
                        statusLabel.setText("⏸ 已暂停");
                        timeLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;");
                    } else {
                        statusLabel.setText("准备开始");
                        timeLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: #3498db;");
                    }

                    // 检测到工作完成
                    if (lastStatus.contains("工作中") && status.contains("休息中")) {
                        completedCount++;
                        totalWorkMinutes += 25;
                        completedCountLabel.setText(String.valueOf(completedCount));
                        workTimeLabel.setText(String.valueOf(totalWorkMinutes));
                    }

                    lastStatus = status;

                    // 更新按钮状态
                    boolean running = pomodoroTimerService.isRunning();
                    updateButtonStates(running);
                });
            }
        }, 0, 500); // 每500ms更新一次
    }

    /**
     * 控制器销毁时清理资源
     */
    public void destroy() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.cancel();
            uiUpdateTimer = null;
        }
    }
}
