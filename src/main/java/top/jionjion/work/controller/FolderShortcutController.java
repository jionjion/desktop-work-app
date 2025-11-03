package top.jionjion.work.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.jionjion.work.WorkApplication;
import top.jionjion.work.entity.FolderShortcut;
import top.jionjion.work.service.FolderShortcutService;
import top.jionjion.work.supper.component.DialogService;

import java.net.URL;
import java.util.List;

/**
 * 文件夹快捷访问磁贴界面控制器
 *
 * @author Jion
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FolderShortcutController {

    private final FolderShortcutService folderShortcutService;
    private final DialogService dialogService;

    @FXML
    private VBox rootPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane contentPane;

    @FXML
    private GridPane tilesGrid;

    @FXML
    private Button configButton;

    @FXML
    private Label titleLabel;

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        log.info("初始化文件夹快捷访问磁贴界面");
        setupUI();
        loadTiles();
    }

    /**
     * 设置UI组件
     */
    private void setupUI() {
        // 配置ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);

        // 配置GridPane
        tilesGrid.setHgap(15);
        tilesGrid.setVgap(15);
        tilesGrid.setPadding(new Insets(20));
        tilesGrid.setAlignment(Pos.CENTER_LEFT);

        // 配置按钮事件
        configButton.setOnAction(e -> openConfigPage());
    }

    /**
     * 加载磁贴数据
     */
    private void loadTiles() {
        // 清空现有磁贴
        tilesGrid.getChildren().clear();

        // 查询数据
        List<FolderShortcut> shortcuts = folderShortcutService.getAllSortedByAccess();

        // 响应式列数计算
        int columns = calculateColumns();

        // 创建磁贴
        for (int i = 0; i < shortcuts.size(); i++) {
            FolderShortcut shortcut = shortcuts.get(i);
            VBox tile = createTile(shortcut);

            int row = i / columns;
            int col = i % columns;
            tilesGrid.add(tile, col, row);
        }

        log.info("加载了 {} 个文件夹快捷方式磁贴", shortcuts.size());
    }

    /**
     * 计算网格列数（根据窗口宽度）
     */
    private int calculateColumns() {
        double width = rootPane.getWidth();
        if (width < 800) {
            return 3;
        } else if (width < 1200) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 创建单个磁贴
     */
    private VBox createTile(FolderShortcut shortcut) {
        VBox tile = new VBox(10);
        tile.setPrefSize(200, 120);
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().addAll("folder-tile", "clickable");
        tile.setPadding(new Insets(15));

        // 创建图标
        ImageView icon = createIcon();

        // 创建名称标签
        Label nameLabel = new Label(shortcut.getName());
        nameLabel.getStyleClass().add("folder-tile-name");
        nameLabel.setMaxWidth(170);
        nameLabel.setStyle("-fx-text-overflow: ellipsis;");

        // 创建路径标签
        Label pathLabel = new Label(shortcut.getPath());
        pathLabel.getStyleClass().add("folder-tile-path");
        pathLabel.setMaxWidth(170);
        pathLabel.setStyle("-fx-text-overflow: ellipsis;");

        // 创建访问次数徽章
        Label badge = new Label(String.valueOf(shortcut.getAccessCount()));
        badge.getStyleClass().add("folder-tile-badge");

        // 使用StackPane叠加徽章
        StackPane iconContainer = new StackPane(icon, badge);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);

        tile.getChildren().addAll(iconContainer, nameLabel, pathLabel);

        // 点击事件
        tile.setOnMouseClicked(e -> handleTileClick(shortcut));

        // 悬停效果
        tile.setOnMouseEntered(e -> tile.getStyleClass().add("folder-tile-hover"));
        tile.setOnMouseExited(e -> tile.getStyleClass().remove("folder-tile-hover"));

        return tile;
    }

    /**
     * 创建文件夹图标
     */
    private ImageView createIcon() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setPreserveRatio(true);

        try {
            Image image = new Image(getClass().getResourceAsStream("/icon/folder.png"));
            imageView.setImage(image);
        } catch (Exception e) {
            log.warn("加载文件夹图标失败，使用默认图标", e);
            // 如果图标加载失败，可以设置默认文本标签
        }

        return imageView;
    }

    /**
     * 处理磁贴点击事件
     */
    private void handleTileClick(FolderShortcut shortcut) {
        Platform.runLater(() -> {
            try {
                log.info("用户点击文件夹磁贴: {}", shortcut.getName());
                folderShortcutService.openFolder(shortcut);

                // 刷新磁贴显示（更新访问次数）
                loadTiles();
            } catch (Exception e) {
                log.error("打开文件夹失败", e);
                String errorMsg = e.getMessage();

                // 提供错误恢复选项
                boolean deleteShortcut = dialogService.confirm("错误",
                        errorMsg + "\n\n是否删除此快捷方式?");

                if (deleteShortcut) {
                    folderShortcutService.delete(shortcut.getId());
                    loadTiles();
                    dialogService.info("提示", "已删除失效的快捷方式");
                }
            }
        });
    }

    /**
     * 打开配置页面
     */
    private void openConfigPage() {
        log.info("打开文件夹快捷访问配置页面");
        try {
            FXMLLoader loader = new FXMLLoader();
            if (WorkApplication.getApplicationContext() != null) {
                loader.setControllerFactory(WorkApplication.getApplicationContext()::getBean);
            }
            URL resource = getClass().getResource("/templates/folder-shortcut-config.fxml");
            if (resource == null) {
                dialogService.error("错误", "未找到配置页面模板: /templates/folder-shortcut-config.fxml");
                return;
            }
            loader.setLocation(resource);
            Node configView = loader.load();
            // 将配置页面渲染到当前根容器
            contentPane.getChildren().setAll(configView);

            // 更新标题与按钮为“返回”
            titleLabel.setText("文件夹快捷访问配置");
            configButton.setText("返回");
            configButton.setOnAction(e -> closeConfigPage());

            log.info("已切换到文件夹配置管理页面");
        } catch (Exception e) {
            log.error("打开配置页面失败", e);
            dialogService.error("错误", "打开配置页面失败: " + e.getMessage());
        }
    }

    /**
     * 关闭配置页面，返回磁贴列表
     */
    private void closeConfigPage() {
        log.info("返回文件快捷访问磁贴列表");
        // 复原中心区域为磁贴滚动视图
        contentPane.getChildren().setAll(scrollPane);

        // 复原标题与按钮
        titleLabel.setText("文件快捷访问");
        configButton.setText("⚙ 配置");
        configButton.setOnAction(e -> openConfigPage());

        // 刷新磁贴
        loadTiles();
    }

    /**
     * 刷新磁贴列表（供外部调用）
     */
    public void refresh() {
        loadTiles();
    }
}
