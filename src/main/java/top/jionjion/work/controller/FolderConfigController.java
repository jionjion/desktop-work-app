package top.jionjion.work.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.jionjion.work.entity.FolderShortcut;
import top.jionjion.work.service.FolderShortcutService;
import top.jionjion.work.supper.component.DialogService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件夹快捷访问配置界面控制器
 *
 * @author Jion
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FolderConfigController {

    private final FolderShortcutService folderShortcutService;
    private final DialogService dialogService;

    @FXML
    private TextField nameField;

    @FXML
    private TextField pathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<FolderShortcut> folderTable;

    @FXML
    private TableColumn<FolderShortcut, Long> idColumn;

    @FXML
    private TableColumn<FolderShortcut, String> nameColumn;

    @FXML
    private TableColumn<FolderShortcut, String> pathColumn;

    @FXML
    private TableColumn<FolderShortcut, Integer> accessCountColumn;

    @FXML
    private TableColumn<FolderShortcut, String> lastAccessTimeColumn;

    private ObservableList<FolderShortcut> folderList = FXCollections.observableArrayList();

    private FolderShortcut selectedFolder;

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        log.info("初始化文件夹配置管理界面");
        setupTable();
        setupButtons();
        loadData();
    }

    /**
     * 配置表格
     */
    private void setupTable() {
        // 配置列绑定
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        accessCountColumn.setCellValueFactory(new PropertyValueFactory<>("accessCount"));

        // 最后访问时间需要格式化
        lastAccessTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getLastAccessTime();
            if (time == null) {
                return new javafx.beans.property.SimpleStringProperty("从未访问");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return new javafx.beans.property.SimpleStringProperty(time.format(formatter));
        });

        // 设置数据源
        folderTable.setItems(folderList);

        // 选中行事件
        folderTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedFolder = newSelection;
                    fillForm(newSelection);
                }
            }
        );

        // 设置列宽策略
        idColumn.setPrefWidth(60);
        nameColumn.setPrefWidth(150);
        pathColumn.setPrefWidth(300);
        accessCountColumn.setPrefWidth(100);
        lastAccessTimeColumn.setPrefWidth(150);
    }

    /**
     * 配置按钮事件
     */
    private void setupButtons() {
        browseButton.setOnAction(e -> handleBrowse());
        addButton.setOnAction(e -> handleAdd());
        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClear());
    }

    /**
     * 加载数据
     */
    private void loadData() {
        folderList.clear();
        folderList.addAll(folderShortcutService.getAllSortedByAccess());
        log.info("加载了 {} 条文件夹快捷方式配置", folderList.size());
    }

    /**
     * 填充表单
     */
    private void fillForm(FolderShortcut folder) {
        nameField.setText(folder.getName());
        pathField.setText(folder.getPath());
    }

    /**
     * 清空表单
     */
    private void handleClear() {
        nameField.clear();
        pathField.clear();
        selectedFolder = null;
        folderTable.getSelectionModel().clearSelection();
    }

    /**
     * 浏览文件夹
     */
    private void handleBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件夹");

        // 设置初始目录
        String currentPath = pathField.getText();
        if (currentPath != null && !currentPath.isEmpty()) {
            File initialDir = new File(currentPath);
            if (initialDir.exists() && initialDir.isDirectory()) {
                directoryChooser.setInitialDirectory(initialDir);
            }
        }

        // 显示对话框
        File selectedDirectory = directoryChooser.showDialog(pathField.getScene().getWindow());
        if (selectedDirectory != null) {
            pathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * 添加快捷方式
     */
    private void handleAdd() {
        // 表单验证
        if (!validateForm()) {
            return;
        }

        String name = nameField.getText().trim();
        String path = pathField.getText().trim();

        // 检查路径是否已存在
        if (folderShortcutService.existsByPath(path)) {
            dialogService.warn("提示", "该路径已经添加，无需重复操作");
            return;
        }

        // 创建实体
        FolderShortcut folder = new FolderShortcut();
        folder.setName(name);
        folder.setPath(path);
        folder.setAccessCount(0);
        folder.setCreateTime(LocalDateTime.now());

        // 保存
        try {
            folderShortcutService.add(folder);
            dialogService.info("成功", "添加成功");
            loadData();
            handleClear();
        } catch (Exception e) {
            log.error("添加快捷方式失败", e);
            dialogService.error("错误", "添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新快捷方式
     */
    private void handleUpdate() {
        // 检查是否选中
        if (selectedFolder == null) {
            dialogService.warn("提示", "请先选择要更新的记录");
            return;
        }

        // 表单验证
        if (!validateForm()) {
            return;
        }

        String name = nameField.getText().trim();
        String path = pathField.getText().trim();

        // 检查路径是否与其他记录重复
        if (folderShortcutService.existsByPathExcludingId(path, selectedFolder.getId())) {
            dialogService.warn("提示", "该路径已被其他记录使用");
            return;
        }

        // 更新实体
        selectedFolder.setName(name);
        selectedFolder.setPath(path);

        // 保存
        try {
            folderShortcutService.update(selectedFolder);
            dialogService.info("成功", "更新成功");
            loadData();
            handleClear();
        } catch (Exception e) {
            log.error("更新快捷方式失败", e);
            dialogService.error("错误", "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除快捷方式
     */
    private void handleDelete() {
        // 检查是否选中
        if (selectedFolder == null) {
            dialogService.warn("提示", "请先选择要删除的记录");
            return;
        }

        // 确认删除
        boolean confirmed = dialogService.confirm("确认", 
            "确定要删除快捷方式 \"" + selectedFolder.getName() + "\" 吗？");

        if (!confirmed) {
            return;
        }

        // 删除
        try {
            folderShortcutService.delete(selectedFolder.getId());
            dialogService.info("成功", "删除成功");
            loadData();
            handleClear();
        } catch (Exception e) {
            log.error("删除快捷方式失败", e);
            dialogService.error("错误", "删除失败: " + e.getMessage());
        }
    }

    /**
     * 表单验证
     */
    private boolean validateForm() {
        String name = nameField.getText();
        String path = pathField.getText();

        // 非空验证
        if (name == null || name.trim().isEmpty() || path == null || path.trim().isEmpty()) {
            dialogService.warn("提示", "请填写所有字段");
            return false;
        }

        // 长度验证
        if (name.trim().length() > 200) {
            dialogService.warn("提示", "文件夹名称不能超过200个字符");
            return false;
        }

        if (path.trim().length() > 500) {
            dialogService.warn("提示", "文件夹路径不能超过500个字符");
            return false;
        }

        // 路径有效性验证
        if (!folderShortcutService.isPathValid(path.trim())) {
            dialogService.warn("提示", "路径不存在或无法访问");
            return false;
        }

        return true;
    }
}
