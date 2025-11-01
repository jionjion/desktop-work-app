package top.jionjion.work.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Base64工具模板控制器
 * 
 * @author Jion
 */
@Component
public class Base64ToolTemplateController implements Initializable {
    
    @FXML private Label titleLabel;
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private Button encodeBtn;
    @FXML private Button decodeBtn;
    @FXML private Button clearBtn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 可以在这里添加初始化逻辑
    }
    
    @FXML
    private void onEncode() {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            outputArea.setText("请输入要编码的文本");
            return;
        }
        
        try {
            String encoded = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
            outputArea.setText(encoded);
        } catch (Exception e) {
            outputArea.setText("编码失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void onDecode() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("请输入要解码的Base64字符串");
            return;
        }
        
        try {
            byte[] decoded = Base64.getDecoder().decode(input);
            String result = new String(decoded, StandardCharsets.UTF_8);
            outputArea.setText(result);
        } catch (Exception e) {
            outputArea.setText("解码失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void onClear() {
        inputArea.clear();
        outputArea.clear();
    }
}