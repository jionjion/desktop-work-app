package top.jionjion.work.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import top.jionjion.work.service.DailyReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        copyBtn.setText(originalText);
                        copyBtn.setDisable(false);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    /**
     * 保存日报
     */
    @FXML
    private void onSaveReport() {
        // 获取内容
        String content = workReportContentArea.getText();
        dailyReportService.seveTodayReport(content);
    }
}