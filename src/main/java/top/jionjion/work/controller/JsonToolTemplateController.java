package top.jionjion.work.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JSON工具模板控制器
 * 
 * @author Jion
 */
@Component
public class JsonToolTemplateController implements Initializable {
    
    @FXML private Label titleLabel;
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private Button formatBtn;
    @FXML private Button compressBtn;
    @FXML private Button validateBtn;
    @FXML private Button clearBtn;
    @FXML private Label statusLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 监听输入变化，实时更新状态
        inputArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().isEmpty()) {
                statusLabel.setText("就绪");
                statusLabel.getStyleClass().removeAll("text-success", "text-danger");
                statusLabel.getStyleClass().add("text-muted");
            }
        });
    }
    
    @FXML
    private void onFormatJson() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            updateStatus("请输入JSON内容", false);
            return;
        }
        
        try {
            // 简单的JSON格式化（使用空格和换行）
            String formatted = formatJsonString(input);
            outputArea.setText(formatted);
            updateStatus("JSON格式化成功", true);
        } catch (Exception e) {
            outputArea.setText("格式化失败：" + e.getMessage());
            updateStatus("JSON格式错误", false);
        }
    }
    
    @FXML
    private void onCompressJson() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            updateStatus("请输入JSON内容", false);
            return;
        }
        
        try {
            // 简单的JSON压缩（去除空格和换行）
            String compressed = compressJsonString(input);
            outputArea.setText(compressed);
            updateStatus("JSON压缩成功", true);
        } catch (Exception e) {
            outputArea.setText("压缩失败：" + e.getMessage());
            updateStatus("JSON格式错误", false);
        }
    }
    
    @FXML
    private void onValidateJson() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            updateStatus("请输入JSON内容", false);
            return;
        }
        
        try {
            // 简单的JSON验证
            if (isValidJson(input)) {
                outputArea.setText("✓ JSON格式正确");
                updateStatus("JSON验证通过", true);
            } else {
                outputArea.setText("✗ JSON格式错误");
                updateStatus("JSON验证失败", false);
            }
        } catch (Exception e) {
            outputArea.setText("✗ JSON格式错误：\n" + e.getMessage());
            updateStatus("JSON验证失败", false);
        }
    }
    
    @FXML
    private void onClearContent() {
        inputArea.clear();
        outputArea.clear();
        updateStatus("就绪", null);
    }
    
    private void updateStatus(String message, Boolean success) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("text-success", "text-danger", "text-muted");
        
        if (success == null) {
            statusLabel.getStyleClass().add("text-muted");
        } else if (success) {
            statusLabel.getStyleClass().add("text-success");
        } else {
            statusLabel.getStyleClass().add("text-danger");
        }
    }
    
    /**
     * 简单的JSON格式化方法
     */
    private String formatJsonString(String json) {
        // 这里实现简单的格式化逻辑
        StringBuilder formatted = new StringBuilder();
        int indent = 0;
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char ch = json.charAt(i);
            
            if (ch == '"' && (i == 0 || json.charAt(i-1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                switch (ch) {
                    case '{', '[' -> {
                        formatted.append(ch).append('\n');
                        indent++;
                        appendIndent(formatted, indent);
                    }
                    case '}', ']' -> {
                        formatted.append('\n');
                        indent--;
                        appendIndent(formatted, indent);
                        formatted.append(ch);
                    }
                    case ',' -> {
                        formatted.append(ch).append('\n');
                        appendIndent(formatted, indent);
                    }
                    case ':' -> formatted.append(ch).append(' ');
                    default -> {
                        if (!Character.isWhitespace(ch)) {
                            formatted.append(ch);
                        }
                    }
                }
            } else {
                formatted.append(ch);
            }
        }
        
        return formatted.toString();
    }
    
    /**
     * 简单的JSON压缩方法
     */
    private String compressJsonString(String json) {
        StringBuilder compressed = new StringBuilder();
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char ch = json.charAt(i);
            
            if (ch == '"' && (i == 0 || json.charAt(i-1) != '\\')) {
                inString = !inString;
            }
            
            if (inString || !Character.isWhitespace(ch)) {
                compressed.append(ch);
            }
        }
        
        return compressed.toString();
    }
    
    /**
     * 简单的JSON验证方法
     */
    private boolean isValidJson(String json) {
        // 简单的括号匹配验证
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char ch = json.charAt(i);
            
            if (ch == '"' && (i == 0 || json.charAt(i-1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                switch (ch) {
                    case '{' -> braceCount++;
                    case '}' -> braceCount--;
                    case '[' -> bracketCount++;
                    case ']' -> bracketCount--;
                }
                
                if (braceCount < 0 || bracketCount < 0) {
                    return false;
                }
            }
        }
        
        return braceCount == 0 && bracketCount == 0;
    }
    
    /**
     * 添加缩进
     */
    private void appendIndent(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }
}