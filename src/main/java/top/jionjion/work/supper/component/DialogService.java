package top.jionjion.work.supper.component;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.springframework.stereotype.Component;
import top.jionjion.work.config.AppConfig;
import top.jionjion.work.util.RouterManager;

import java.io.InputStream;
import java.util.Optional;

/**
 * 对话框服务
 *
 * @author Jion
 */
@Component
public class DialogService {


    /**
     * 显示信息对话框
     *
     * @param title   标题
     * @param content 内容
     */
    public void info(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        attachOwner(alert);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * 显示警告对话框
     *
     * @param title   标题
     * @param content 内容
     */
    public void warn(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        attachOwner(alert);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * 显示错误对话框
     *
     * @param title   标题
     * @param content 内容
     */
    public void error(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        attachOwner(alert);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * 显示确认对话框
     *
     * @param title   标题
     * @param content 内容
     * @return 是否确认
     */
    public boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        attachOwner(alert);
        styleAlert(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * 显示提示对话框
     *
     * @param title      标题
     * @param header     头部
     * @param defaultValue 默认值
     * @return 输入的值
     */
    public Optional<String> prompt(String title, String header, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(null);
        attachOwner(dialog);
        styleDialog(dialog.getDialogPane());
        return dialog.showAndWait();
    }

    /**
     * 显示自定义对话框
     *
     * @param title   标题
     * @param header  头部
     * @param content 内容
     * @param buttons 按钮
     * @return 按钮类型
     */
    public Optional<ButtonType> custom(String title, String header, Node content, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getButtonTypes().setAll(buttons.length > 0 ? buttons : new ButtonType[]{ButtonType.OK});
        alert.getDialogPane().setContent(content);
        attachOwner(alert);
        styleAlert(alert);
        return alert.showAndWait();
    }

    /**
     * 样式对话框
     *
     * @param alert 对话框
     */
    private void styleAlert(Alert alert) {
        styleDialog(alert.getDialogPane());
        addIcon(alert.getDialogPane());
        styleButtons(alert.getDialogPane());
    }

    /**
     * 样式对话框
     *
     * @param dialogPane 对话框
     */
    private void styleDialog(DialogPane dialogPane) {
        dialogPane.setId("app-dialog");
        dialogPane.setMinWidth(420);
        dialogPane.setPrefWidth(520);

        dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStylesheets().add(AppConfig.appStylesheet());
        dialogPane.getStylesheets().add(AppConfig.mainStylesheet());
        dialogPane.getStyleClass().add("alert");

        styleButtons(dialogPane);
    }

    /**
     * 添加对话框的拥有者
     *
     * @param dialog 对话框
     */
    private void attachOwner(Dialog<?> dialog) {
        Stage owner = RouterManager.getStage();
        if (owner != null) {
            dialog.initOwner(owner);
        }
    }

    /**
     * 为对话框添加图标
     *
     * @param dialogPane 对话框
     */
    private void addIcon(DialogPane dialogPane) {
        Window window = dialogPane.getScene() != null ? dialogPane.getScene().getWindow() : null;
        if (window instanceof Stage stage) {
            InputStream iconStream = AppConfig.class.getResourceAsStream(AppConfig.ICON_PATH);
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        }
    }

    /**
     * 统一按钮的BootstrapFX风格与尺寸
     */
    private void styleButtons(DialogPane dialogPane) {
        for (ButtonType bt : dialogPane.getButtonTypes()) {
            javafx.scene.control.Button btn = (javafx.scene.control.Button) dialogPane.lookupButton(bt);
            if (btn == null) {
                continue;
            }
            // 基础按钮风格
            btn.getStyleClass().add("btn");

            // 类型映射
            if (bt == ButtonType.OK) {
                btn.getStyleClass().add("btn-primary");
            } else if (bt == ButtonType.CANCEL) {
                btn.getStyleClass().add("btn-secondary");
            } else if (bt == ButtonType.YES) {
                btn.getStyleClass().add("btn-success");
            } else if (bt == ButtonType.NO) {
                btn.getStyleClass().add("btn-outline-secondary");
            } else if (bt == ButtonType.CLOSE) {
                btn.getStyleClass().add("btn-light");
            } else if (bt == ButtonType.APPLY) {
                btn.getStyleClass().add("btn-success");
            } else {
                // 其他类型统一为浅色按钮
                btn.getStyleClass().add("btn-light");
            }

            // 尺寸与可读性
            btn.setMinWidth(86);
            btn.setPrefHeight(32);
        }
    }
}
