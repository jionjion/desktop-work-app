package top.jionjion.work.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import top.jionjion.work.entity.CodeDirectory;
import top.jionjion.work.repository.CodeDirectoryRepository;

/**
 * 代码目录配置控制器
 *
 * @author Jion
 */
@Component
public class CodeDirectoryConfigController implements Initializable {

    @Autowired
    private CodeDirectoryRepository codeDirectoryRepository;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField pathField;

    @FXML
    private Button browseBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private TableView<CodeDirectory> directoryTableView;

    @FXML
    private TableColumn<CodeDirectory, Long> idColumn;

    @FXML
    private TableColumn<CodeDirectory, String> nameColumn;

    @FXML
    private TableColumn<CodeDirectory, String> typeColumn;

    @FXML
    private TableColumn<CodeDirectory, String> pathColumn;

    // 数据列表
    private ObservableList<CodeDirectory> directoryItems;

    // 当前选中的项目
    private CodeDirectory selectedDirectory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化类型下拉框
        typeComboBox.setItems(FXCollections.observableArrayList(
                "前端", "后端", "移动端", "桌面端", "其他"));

        // 初始化表格列
        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        typeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        pathColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPath()));

        // 初始化数据列表
        directoryItems = FXCollections.observableArrayList();
        directoryTableView.setItems(directoryItems);

        // 加载所有数据
        loadDirectoryData();

        // 设置事件监听器
        setupEventListeners();
    }

    /**
     * 加载所有目录数据
     */
    private void loadDirectoryData() {
        directoryItems.clear();
        // 查询并根据名称排序
        directoryItems.addAll(codeDirectoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, CodeDirectory.NAME)
                        .and(Sort.by(Sort.Direction.ASC, CodeDirectory.TYPE))));
    }

    /**
     * 设置事件监听器
     */
    private void setupEventListeners() {
        // 表格选择监听
        directoryTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedDirectory = newValue;
                    if (selectedDirectory != null) {
                        populateFields(selectedDirectory);
                    }
                });

        // 添加按钮
        addBtn.setOnAction(event -> addDirectory());

        // 更新按钮
        updateBtn.setOnAction(event -> updateDirectory());

        // 删除按钮
        deleteBtn.setOnAction(event -> deleteDirectory());

        // 清空按钮
        clearBtn.setOnAction(event -> clearFields());

        // 浏览按钮
        browseBtn.setOnAction(event -> browsePath());
    }

    /**
     * 填充表单字段
     */
    private void populateFields(CodeDirectory directory) {
        nameField.setText(directory.getName());
        typeComboBox.setValue(directory.getType());
        pathField.setText(directory.getPath());
    }

    /**
     * 清空表单字段
     */
    private void clearFields() {
        nameField.clear();
        typeComboBox.setValue(null);
        pathField.clear();
        selectedDirectory = null;
        directoryTableView.getSelectionModel().clearSelection();
    }

    /**
     * 添加新目录
     */
    private void addDirectory() {
        String name = nameField.getText().trim();
        String type = typeComboBox.getValue();
        String path = pathField.getText().trim();

        if (name.isEmpty() || type == null || path.isEmpty()) {
            showAlert("错误", "请填写所有字段");
            return;
        }

        CodeDirectory directory = new CodeDirectory();
        directory.setName(name);
        directory.setType(type);
        directory.setPath(path);

        CodeDirectory saved = codeDirectoryRepository.save(directory);
        directoryItems.add(saved);
        clearFields();
        showAlert("成功", "项目已添加");
    }

    /**
     * 更新目录
     */
    private void updateDirectory() {
        if (selectedDirectory == null) {
            showAlert("错误", "请选择要更新的项目");
            return;
        }

        String name = nameField.getText().trim();
        String type = typeComboBox.getValue();
        String path = pathField.getText().trim();

        if (name.isEmpty() || type == null || path.isEmpty()) {
            showAlert("错误", "请填写所有字段");
            return;
        }

        selectedDirectory.setName(name);
        selectedDirectory.setType(type);
        selectedDirectory.setPath(path);

        CodeDirectory updated = codeDirectoryRepository.save(selectedDirectory);

        // 更新表格显示
        int index = directoryItems.indexOf(selectedDirectory);
        if (index >= 0) {
            directoryItems.set(index, updated);
        }

        showAlert("成功", "项目已更新");
    }

    /**
     * 删除目录
     */
    private void deleteDirectory() {
        if (selectedDirectory == null) {
            showAlert("错误", "请选择要删除的项目");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认删除");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("确定要删除项目 \"" + selectedDirectory.getName() + "\" 吗？");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                codeDirectoryRepository.delete(selectedDirectory);
                directoryItems.remove(selectedDirectory);
                clearFields();
                showAlert("成功", "项目已删除");
            }
        });
    }

    /**
     * 浏览路径
     */
    private void browsePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择项目目录");

        // 设置初始目录（如果路径字段不为空）
        String currentPath = pathField.getText();
        if (!currentPath.isEmpty()) {
            java.io.File initialDir = new java.io.File(currentPath);
            if (initialDir.exists()) {
                directoryChooser.setInitialDirectory(initialDir);
            }
        }

        // 显示目录选择对话框
        java.io.File selectedDir = directoryChooser.showDialog(pathField.getScene().getWindow());
        if (selectedDir != null) {
            pathField.setText(selectedDir.getAbsolutePath());
        }
    }

    /**
     * 显示提示信息
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}