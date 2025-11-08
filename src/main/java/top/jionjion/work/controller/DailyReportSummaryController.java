package top.jionjion.work.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import top.jionjion.work.entity.DailyReport;
import top.jionjion.work.service.DailyReportService;

/**
 * 日志管理控制器
 * 
 * @author Jion
 */
@Slf4j
@Component
public class DailyReportSummaryController implements Initializable {

    @FXML
    private Label currentMonthLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button prevMonthBtn;

    @FXML
    private Button nextMonthBtn;

    @FXML
    private Label formTitleLabel;

    @FXML
    private Label formSubTitleLabel;

    @FXML
    private RadioButton dailyReportRadio;

    @FXML
    private RadioButton weeklyReportRadio;

    @FXML
    private ToggleGroup reportTypeGroup;

    @FXML
    private TextArea reportContentArea;

    @FXML
    private Button saveBtn;

    @FXML
    private Button copyBtn;

    @Autowired
    private DailyReportService dailyReportService;

    private YearMonth currentYearMonth;

    // 当前选中的日期
    private LocalDate selectedDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化当前年月
        currentYearMonth = YearMonth.now();
        updateCalendar();

        // 设置按钮事件
        prevMonthBtn.setOnAction(event -> showPrevMonth());
        nextMonthBtn.setOnAction(event -> showNextMonth());

        // 设置表单按钮事件
        saveBtn.setOnAction(event -> saveReport());
        copyBtn.setOnAction(event -> copyReport());

        // 设置报告类型切换事件
        dailyReportRadio.setOnAction(event -> updateFormTitle());
        weeklyReportRadio.setOnAction(event -> updateFormTitle());

        // 为ToggleGroup添加监听器
        reportTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateFormTitle();
            }
        });
    }

    /**
     * 更新日历显示
     */
    private void updateCalendar() {
        // 更新标题，添加前导零
        String monthStr = String.format("%02d", currentYearMonth.getMonthValue());
        currentMonthLabel.setText(currentYearMonth.getYear() + "年" + monthStr + "月");

        // 清空现有的日历网格
        calendarGrid.getChildren().clear();

        // 获取该月的第一天是星期几 (周一作为第一天)
        LocalDate firstDay = currentYearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

        // 调整索引，使周一为第一列 (0=Monday, 1=Tuesday, ..., 6=Sunday)
        // 由于我们把周一放在第一列，需要重新计算起始位置
        // Monday=1 -> index=0, Tuesday=2 -> index=1, ..., Sunday=7 -> index=6
        // 所以计算公式为: (dayOfWeek - 1)
        int startDayIndex = dayOfWeek - 1;

        // 获取该月的总天数
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // 填充日历网格
        int row = 0;
        int col = startDayIndex;

        for (int day = 1; day <= daysInMonth; day++) {
            // 创建日期按钮
            Button dayButton = createDayButton(day);

            // 将按钮添加到网格中，居中显示
            GridPane.setHalignment(dayButton, javafx.geometry.HPos.CENTER);
            GridPane.setValignment(dayButton, javafx.geometry.VPos.CENTER);
            calendarGrid.add(dayButton, col, row);

            // 更新行列索引
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * 创建日期按钮
     */
    private Button createDayButton(int day) {
        Button button = new Button(String.valueOf(day));
        // 移除固定的宽高设置，让按钮自动填满网格单元格
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        button.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 去掉点击动画效果，设置为无伪状态
        button.setFocusTraversable(false);
        button.setMouseTransparent(false);
        // 移除按钮的默认效果
        button.setOpacity(1.0);

        // 设置简单的框框样式，没有背景色填充
        button.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-border-color: #adb5bd; -fx-border-width: 1px; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");

        // 为周末添加特殊背景色和边框颜色
        LocalDate date = currentYearMonth.atDay(day);
        if (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            // 为周末设置浅红色背景
            button.setStyle(
                    "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #fff5f5; -fx-border-color: #ff6b6b; -fx-border-width: 1px; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");
        }

        // 去除按钮的所有效果和动画
        button.setEffect(null);
        button.setBlendMode(null);

        // 确保按钮没有悬停效果
        button.setOnMouseEntered(e -> {
        });
        button.setOnMouseExited(e -> {
        });

        // 设置按钮点击事件
        button.setOnAction(event -> onDaySelected(day));

        return button;
    }

    /**
     * 当选择某一天时的处理
     */
    private void onDaySelected(int day) {
        selectedDate = currentYearMonth.atDay(day);

        // 更新表单标题和副标题
        updateFormTitle();

        // 从数据库查询该日期的日报
        if (dailyReportRadio.isSelected()) {
            // 日报模式
            DailyReport report = dailyReportService.findByReportDate(selectedDate);
            if (report != null && report.getContent() != null && !report.getContent().isEmpty()) {
                reportContentArea.setText(report.getContent());
            } else {
                reportContentArea.setText("请在此处填写" + selectedDate + "的日报内容...");
            }
            // 更新日报副标题为选中的日期
            formSubTitleLabel.setText(selectedDate.toString() + " 日报");
        } else {
            // 计算该周的开始和结束日期 (周一到周日)
            java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.of(java.time.DayOfWeek.MONDAY, 1);
            LocalDate weekStart = selectedDate.with(weekFields.dayOfWeek(), 1);
            LocalDate weekEnd = selectedDate.with(weekFields.dayOfWeek(), 7);
            int weekOfYear = selectedDate.get(weekFields.weekOfYear());
            reportContentArea
                    .setText("请在此处填写 " + weekStart + " 到 " + weekEnd + " 的第" + weekOfYear + "周周报内容...");
            // 更新周报副标题
            formSubTitleLabel.setText("第" + weekOfYear + "周");
        }
    }

    /**
     * 更新表单标题
     */
    private void updateFormTitle() {
        if (dailyReportRadio.isSelected()) {
            formTitleLabel.setText("日报详情");
            // 显示日报副标题
            formSubTitleLabel.setVisible(true);
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            formSubTitleLabel.setText(currentDate.toString() + " 日报");
        } else {
            formTitleLabel.setText("周报详情");
            // 显示周报副标题
            if (currentYearMonth != null) {
                // 获取当前日期的周数
                java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.of(java.time.DayOfWeek.MONDAY,
                        1);
                LocalDate currentDate = java.time.LocalDate.now();
                int weekOfYear = currentDate.get(weekFields.weekOfYear());
                formSubTitleLabel.setText("第" + weekOfYear + "周");
                formSubTitleLabel.setVisible(true);
            } else {
                formSubTitleLabel.setVisible(false);
            }
        }
    }

    /**
     * 显示上一个月
     */
    private void showPrevMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    /**
     * 显示下一个月
     */
    private void showNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    /**
     * 保存报告
     */
    private void saveReport() {
        try {
            // 获取报告内容
            String content = reportContentArea.getText();
            
            if (content == null || content.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("保存失败");
                alert.setHeaderText(null);
                alert.setContentText("报告内容不能为空！");
                alert.showAndWait();
                return;
            }
            
            if (selectedDate == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("保存失败");
                alert.setHeaderText(null);
                alert.setContentText("请先选择一个日期！");
                alert.showAndWait();
                return;
            }
            
            // 保存或更新日报
            if (dailyReportRadio.isSelected()) {
                // 保存日报
                dailyReportService.saveOrUpdateReport(selectedDate, content);
                log.info("保存日报成功: {}", selectedDate);
            } else {
                // TODO: 后续可以支持周报保存
                log.warn("周报保存功能暂未实现");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("保存报告");
            alert.setHeaderText(null);
            alert.setContentText("报告已保存成功！");
            alert.showAndWait();
        } catch (Exception e) {
            log.error("保存报告失败", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("保存失败");
            alert.setHeaderText(null);
            alert.setContentText("保存报告时发生错误: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 复制报告
     */
    private void copyReport() {
        String content = reportContentArea.getText();
        if (!content.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
            clipboardContent.putString(content);
            clipboard.setContent(clipboardContent);

            // 显示提示信息
            String originalText = copyBtn.getText();
            copyBtn.setText("已复制!");

            // 2秒后恢复按钮文本
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        copyBtn.setText(originalText);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}