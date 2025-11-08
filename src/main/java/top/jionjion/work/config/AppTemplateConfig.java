package top.jionjion.work.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import top.jionjion.work.util.TemplateRenderer;

/**
 * 工具模板配置 - 定义各种工具的渲染模板
 * 支持FXML模板和Java组件两种方式
 * 
 * @author Jion
 */
@Component
public class AppTemplateConfig {

    /**
     * FXML模板映射
     */
    private final Map<String, String> fxmlTemplateMap = new HashMap<>();

    /**
     * Java组件模板映射
     */
    private final Map<String, TemplateRenderer.ComponentFactory> componentTemplates = new HashMap<>();

    public AppTemplateConfig() {
        registerFxmlTemplates();
        registerComponentTemplates();
    }

    /**
     * 注册FXML模板映射
     */
    private void registerFxmlTemplates() {
        // 注册FXML模板(模板名 -> FXML文件名)
        fxmlTemplateMap.put("番茄钟", "pomodoro-timer");
        fxmlTemplateMap.put("日报生成", "daily-report");
        fxmlTemplateMap.put("日志管理", "daily-report-summary");
        fxmlTemplateMap.put("项目配置", "code-directory-config");
        fxmlTemplateMap.put("待办记录", "todo-record");
        fxmlTemplateMap.put("JSON工具", "json-tool");
        fxmlTemplateMap.put("Base64编解码", "base64-tool");
        
        // 文件夹快捷访问功能
        fxmlTemplateMap.put("文件快捷访问", "folder-shortcut-tiles");
        fxmlTemplateMap.put("文件夹配置管理", "folder-shortcut-config");
    }

    /**
     * 注册Java组件模板（用于不使用FXML的工具）
     */
    private void registerComponentTemplates() {
        // 注册计算器模板（使用Java组件）
        componentTemplates.put("计算器", this::createCalculatorTemplate);
    }

    /**
     * 获取工具对应的FXML模板名
     */
    public String getFxmlTemplate(String toolName) {
        return fxmlTemplateMap.get(toolName);
    }

    /**
     * 获取工具对应的Java组件工厂
     */
    public TemplateRenderer.ComponentFactory getComponentTemplate(String toolName) {
        return componentTemplates.getOrDefault(toolName, () -> createDefaultTemplate(toolName));
    }

    /**
     * 判断工具是否使用FXML模板
     */
    public boolean usesFxmlTemplate(String toolName) {
        return fxmlTemplateMap.containsKey(toolName);
    }

    /**
     * 计算器模板
     */
    private Node createCalculatorTemplate() {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 20;");
        container.getStyleClass().addAll("bg-white", "rounded", "shadow");

        // 标题
        Label title = new Label("计算器");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        title.getStyleClass().add("text-primary");
        
        // 分隔线
        Separator separator = new Separator();
        HBox separatorBox = new HBox(separator);
        HBox.setHgrow(separator, Priority.ALWAYS);

        // 显示区域
        VBox displayBox = new VBox(8);
        Label displayLabel = new Label("显示屏:");
        displayLabel.getStyleClass().add("text-dark");
        
        TextField displayField = new TextField("0");
        displayField.setEditable(false);
        displayField.setStyle("-fx-font-size: 18px; -fx-alignment: center-right; -fx-min-width: 264px; -fx-pref-width: 264px;");
        displayBox.getChildren().addAll(displayLabel, displayField);

        // 创建计算器按钮网格
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(8);
        buttonGrid.setVgap(8);
        buttonGrid.setStyle("-fx-alignment: center-left;");

        String[] buttons = {
                "C", "±", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "="
        };

        // 简单的计算器状态
        final StringBuilder currentInput = new StringBuilder("0");
        final String[] operator = { "" };
        final double[] firstNumber = { 0 };

        for (int i = 0; i < buttons.length; i++) {
            Button btn = new Button(buttons[i]);
            btn.setPrefSize(60, 60);

            if ("0123456789.".contains(buttons[i])) {
                btn.getStyleClass().addAll("btn", "btn-light");
            } else if ("+-×÷=".contains(buttons[i])) {
                btn.getStyleClass().addAll("btn", "btn-primary");
            } else {
                btn.getStyleClass().addAll("btn", "btn-secondary");
            }

            // 简单的计算器逻辑
            final String buttonText = buttons[i];
            btn.setOnAction(e -> {
                switch (buttonText) {
                    case "C":
                        currentInput.setLength(0);
                        currentInput.append("0");
                        operator[0] = "";
                        firstNumber[0] = 0;
                        displayField.setText("0");
                        break;
                    case "=":
                        if (!operator[0].isEmpty()) {
                            try {
                                double second = Double.parseDouble(currentInput.toString());
                                double result = switch (operator[0]) {
                                    case "+" -> firstNumber[0] + second;
                                    case "-" -> firstNumber[0] - second;
                                    case "×" -> firstNumber[0] * second;
                                    case "÷" -> second != 0 ? firstNumber[0] / second : 0;
                                    default -> second;
                                };
                                displayField.setText(String.valueOf(result));
                                currentInput.setLength(0);
                                currentInput.append(result);
                                operator[0] = "";
                            } catch (NumberFormatException ex) {
                                displayField.setText("错误");
                            }
                        }
                        break;
                    case "+":
                    case "-":
                    case "×":
                    case "÷":
                        if (!currentInput.toString().equals("0")) {
                            firstNumber[0] = Double.parseDouble(currentInput.toString());
                            operator[0] = buttonText;
                            currentInput.setLength(0);
                            currentInput.append("0");
                        }
                        break;
                    default:
                        if (buttonText.matches("[0-9.]")) {
                            if (currentInput.toString().equals("0") && !buttonText.equals(".")) {
                                currentInput.setLength(0);
                            }
                            currentInput.append(buttonText);
                            displayField.setText(currentInput.toString());
                        }
                        break;
                }
            });

            // 根据按钮布局
            if (i < 19) {
                buttonGrid.add(btn, i % 4, i / 4);
            } else {
                // "0" 按钮占两列
                if (buttonText.equals("0")) {
                    buttonGrid.add(btn, 0, 4, 2, 1);
                } else if (buttonText.equals(".")) {
                    buttonGrid.add(btn, 2, 4);
                } else if (buttonText.equals("=")) {
                    buttonGrid.add(btn, 3, 4);
                }
            }
        }

        container.getChildren().addAll(title, separatorBox, displayBox, buttonGrid);
        return container;
    }

    /**
     * 默认模板
     */
    private Node createDefaultTemplate(String toolName) {
        VBox container = new VBox(20);
        container.setStyle("-fx-padding: 40; -fx-alignment: center;");
        container.getStyleClass().addAll("bg-white", "rounded", "shadow");

        Label title = new Label("🔧 " + toolName);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        title.getStyleClass().add("text-primary");

        Label description = new Label("该工具正在开发中...");
        description.setStyle("-fx-font-size: 16px;");
        description.getStyleClass().add("text-muted");

        container.getChildren().addAll(title, description);
        return container;
    }
}