package top.jionjion.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import top.jionjion.work.config.AppStyle;
import top.jionjion.work.config.AppTemplateConfig;
import top.jionjion.work.supper.component.DialogService;
import top.jionjion.work.util.TemplateRenderer;

/**
 * 工具栏控制器 - 使用模板渲染系统
 *
 * @author Jion
 */
@Slf4j
@Component
public class LayoutController {

    @FXML
    private ListView<String> toolList;

    @FXML
    private StackPane mainPanel;

    @Autowired
    private TemplateRenderer templateRenderer;

    @Autowired
    private AppTemplateConfig templateConfig;

    @Autowired
    private DialogService dialogService;

    @FXML
    private void initialize() {
        // 初始化工具列表
        toolList.getItems().addAll(
                "番茄钟",
                "日报生成",
                "日志管理",
                "项目配置",
                "待办记录",
                "文件快捷访问",
                "JSON工具",
                "Base64编解码",
                "计算器");

        // 自定义每一行显示（加图标）
        toolList.setCellFactory(param -> new ListCell<>() {
            private final HBox container = new HBox(10);
            private final ImageView iconView = new ImageView();
            private final Label textLabel = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);
                container.setPadding(new Insets(8, 12, 8, 12));
                container.getChildren().addAll(iconView, textLabel);
                container.getStyleClass().addAll("list-cell-container");

                // 设置图标大小
                iconView.setFitWidth(AppStyle.ICON_FONT_SIZE);
                iconView.setFitHeight(AppStyle.ICON_FONT_SIZE);
                iconView.setPreserveRatio(true);

                // 默认隐藏不需要的组件
                iconView.setVisible(false);
            }

            @Override
            protected void updateItem(String toolName, boolean empty) {
                super.updateItem(toolName, empty);
                if (empty || toolName == null) {
                    setGraphic(null);
                    setText(null);
                    container.getStyleClass().removeAll("bg-light", "text-primary");
                } else {
                    // 先隐藏图标
                    iconView.setVisible(false);

                    // 设置图标（只使用图片图标）
                    String imagePath = switch (toolName) {
                        case "番茄钟" -> "/icon/shijian.png";
                        case "日报生成" -> "/icon/ribao.png";
                        case "日志管理" -> "/icon/rili.png";
                        case "项目配置" -> "/icon/peizhi.png";
                        case "待办记录" -> "/icon/daiban.png";
                        case "文件快捷访问" -> "/icon/folder.png";
                        case "JSON工具" -> "/icon/json.png";
                        case "Base64编解码" -> "/icon/base64.png";
                        case "计算器" -> "/icon/jisuanqi.png";

                        default -> "/icon/icon.png"; // 默认图标
                    };

                    // 使用图片图标
                    try {
                        Image iconImage = new Image(LayoutController.class.getResourceAsStream(imagePath));
                        iconView.setImage(iconImage);
                        iconView.setVisible(true);
                    } catch (Exception e) {
                        log.error("图片加载失败：{}", e.getMessage());
                        // 如果图片加载失败，使用默认图标
                        try {
                            Image defaultImage = new Image(LayoutController.class.getResourceAsStream("/icon/icon.png"));
                            iconView.setImage(defaultImage);
                            iconView.setVisible(true);
                        } catch (Exception ex) {
                            log.error("默认图片加载失败：{}", ex.getMessage());
                        }
                    }

                    textLabel.setText(toolName);

                    // 设置字体（避免方框）
                    container.setStyle("-fx-font-family: 'Microsoft YaHei UI', sans-serif; -fx-font-size: 14px;");

                    setGraphic(container);
                    // 使用自定义 graphic，不使用默认 text
                    setText(null);

                    // 选中时加样式（在 CSS 中定义）
                    if (isSelected()) {
                        container.getStyleClass().add("bg-light");
                        textLabel.getStyleClass().add("text-primary");
                        textLabel.setStyle("-fx-font-weight: bold;");
                    } else {
                        container.getStyleClass().remove("bg-light");
                        textLabel.getStyleClass().remove("text-primary");
                        textLabel.setStyle("-fx-font-weight: normal;");
                    }
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (selected) {
                    container.getStyleClass().add("bg-light");
                    textLabel.getStyleClass().add("text-primary");
                    textLabel.setStyle("-fx-font-weight: bold;");
                } else {
                    container.getStyleClass().remove("bg-light");
                    textLabel.getStyleClass().remove("text-primary");
                    textLabel.setStyle("-fx-font-weight: normal;");
                }
            }
        });

        // 默认选中第一项
        toolList.getSelectionModel().selectFirst();

        // 点击事件：切换主面板内容
        toolList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showToolContent(newVal);
            }
        });

        // 初始化默认显示
        showToolContent("番茄钟");
    }

    private void showToolContent(String toolName) {
        try {
            // 检查是否使用FXML模板
            if (templateConfig.usesFxmlTemplate(toolName)) {
                // 使用FXML模板渲染
                String templateName = templateConfig.getFxmlTemplate(toolName);
                templateRenderer.renderTemplate(mainPanel, templateName);
            } else {
                // 使用Java组件渲染
                templateRenderer.renderComponent(mainPanel, templateConfig.getComponentTemplate(toolName));
            }
        } catch (Exception e) {
            // 渲染失败时显示错误信息
            mainPanel.getChildren().clear();
            Label errorLabel = new Label("页面加载失败: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            mainPanel.getChildren().add(errorLabel);
        }
    }

    /**
     * 点击帮助按钮
     *
     * @param mouseEvent 鼠标事件
     */
    public void onHelpClicked(MouseEvent mouseEvent) {
        dialogService.info("警告", "你触发了问号!!");
    }
}