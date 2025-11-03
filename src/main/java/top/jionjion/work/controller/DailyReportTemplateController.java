package top.jionjion.work.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jionjion.work.service.DailyReportService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * 日报生成器模板控制器
 *
 * @author Jion
 */
@Component
public class DailyReportTemplateController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private TextArea workReportContentArea;

    @FXML
    private Button generateBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Button copyBtn;

    @FXML
    private Button saveBtn;

    @Autowired
    private DailyReportService dailyReportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化时的设置
        String text = titleLabel.getText();
        titleLabel.setText(text + " " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // 初始时禁用复制按钮
        copyBtn.setDisable(true);
    }

    /**
     * 生成日报
     */
    @FXML
    private void onGenerateReport() {
        String report = dailyReportService.generateReport();
        workReportContentArea.setText(report);

        copyBtn.setDisable(false);
    }

    /**
     * 清空内容
     */
    @FXML
    private void onClearContent() {
        workReportContentArea.clear();

        copyBtn.setDisable(true);
    }

    /**
     * 复制日报
     */
    @FXML
    private void onCopyReport() {
        String reportText = workReportContentArea.getText();
        if (!reportText.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(reportText);
            clipboard.setContent(content);

            // 可以添加一个临时提示
            String originalText = copyBtn.getText();
            copyBtn.setText("已复制!");
            copyBtn.setDisable(true);

            // 2秒后恢复按钮文本
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.seconds(2),
                            event -> {
                                copyBtn.setText(originalText);
                                copyBtn.setDisable(false);
                            }));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    /**
     * 保存日报
     */
    @FXML
    private void onSaveReport() {
        saveBtn.setDisable(true);
        String originalText = saveBtn.getText();

        String content = workReportContentArea.getText();
        dailyReportService.seveTodayReport(content);

        saveBtn.setText("保存成功!");

        // 2秒后恢复按钮文本
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(2),
                        event -> {
                            saveBtn.setDisable(false);
                            saveBtn.setText(originalText);
                        })
        );
        timeline.setCycleCount(1);
        timeline.play();
    }
}